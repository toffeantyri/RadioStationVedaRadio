package ru.music.radiostationvedaradio.services

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.*
import android.media.session.MediaSessionManager
import android.os.*
import android.os.PowerManager.WakeLock
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import ru.music.radiostationvedaradio.R
import ru.music.radiostationvedaradio.busines.model.metadatavedaradio.StreamVedaradioJSONClass
import ru.music.radiostationvedaradio.busines.VedaradioRetrofitApi
import ru.music.radiostationvedaradio.activityes.MainActivity


const val ACTION_PLAY = "ru.music.vedaradio.ACTION_PLAY"
const val ACTION_PAUSE = "ru.music.vedaradio.ACTION_PAUSE"
const val ACTION_CANCEL = "ru.music.vedaradio.ACTION_CANCEL"
const val CHANNEL_ID = "ru.music.vedaradio.ID"
const val NOTIFICATION_ID = 77777


class RadioPlayerService : Service(), MediaPlayer.OnCompletionListener,
    MediaPlayer.OnPreparedListener,
    MediaPlayer.OnErrorListener, MediaPlayer.OnSeekCompleteListener, MediaPlayer.OnInfoListener,
    MediaPlayer.OnBufferingUpdateListener, AudioManager.OnAudioFocusChangeListener {

    inner class LocalBinder : Binder() {
        fun getService(): RadioPlayerService {
            return this@RadioPlayerService
        }
    }

    override fun onBind(intent: Intent): IBinder {
        return iBinder
    }

    private var handler: Handler? = Handler()
    private var job: Job? = null
    private val iBinder: IBinder = LocalBinder()
    private var urlString: String? = null
    private var artist = "Veda Radio"
    private var song = "From Heart to Heart"

    private var resumePosition: Int = 0
    private var mediaPlayer: MediaPlayer? = null

    var STATE_OF_SERVICE = InitStatusMediaPlayer.IDLE
        set(value) {
            field = value
            broadcastTellNewStatus()
            Log.d("MyLogS", "service new state: $value")
        }
    private var audioManager: AudioManager? = null

    private var onIncomingCall = false
    private var phoneStateListener: PhoneStateListener? = null
    private var telephonyManager: TelephonyManager? = null

    private var mediaSessionManager: MediaSessionManager? = null
    private var mediaSession: MediaSessionCompat? = null
    private var transportControls: MediaControllerCompat.TransportControls? = null

    private fun initMediaPlayer() {
        STATE_OF_SERVICE = InitStatusMediaPlayer.INITIALISATION
        mediaPlayer = MediaPlayer()
        mediaPlayer?.apply {
            setOnCompletionListener(this@RadioPlayerService)
            setOnErrorListener(this@RadioPlayerService)
            setOnPreparedListener(this@RadioPlayerService)
            setOnBufferingUpdateListener(this@RadioPlayerService)
            setOnSeekCompleteListener(this@RadioPlayerService)
            setOnInfoListener(this@RadioPlayerService)
            setWakeMode(this@RadioPlayerService, PowerManager.PARTIAL_WAKE_LOCK)
            reset()
            setAudioAttributes(
                AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA).build()
            )
            if (urlString != null) setDataSource(urlString)
        }
        mediaPlayer?.prepareAsync()
    }

    private fun initMediaSession() {
        if (mediaSessionManager != null) return
        mediaSessionManager = getSystemService(Context.MEDIA_SESSION_SERVICE) as MediaSessionManager
        mediaSession = MediaSessionCompat(applicationContext, "RadioPlayer")
        transportControls = mediaSession?.controller?.transportControls
        mediaSession?.isActive = true
        mediaSession?.setFlags(MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS)

        mediaSession?.setCallback(object : MediaSessionCompat.Callback() {
            override fun onPlay() {
                super.onPlay()
                playMedia()
                buildNotification(Playbackstatus.PLAYING)
            }

            override fun onPause() {
                super.onPause()
                pauseMedia()
                buildNotification(Playbackstatus.PAUSED)
            }

            override fun onStop() {
                super.onStop()
                Log.d("MyLogS", "onStopCallBack")
                stopMedia()
                removeNotification()
                stopForeground(true)
                stopSelf()
            }
        })
        Log.d("MyLogS", "initMediaSession")
    }

    private fun updateMetaData() {
        mediaSession?.setMetadata(MediaMetadataCompat.Builder().apply {
            putString(MediaMetadataCompat.METADATA_KEY_ARTIST, artist)
            putString(MediaMetadataCompat.METADATA_KEY_TITLE, song)
        }.build())
    }

    private fun buildNotification(playbackstatus: Playbackstatus): NotificationCompat.Builder {
        var notificationAction = android.R.drawable.ic_media_pause
        var playpauseAction: PendingIntent? = null
        var titleButton: String = ""
        var notRemoveOnSwipe = true
        val contentIntent = Intent(applicationContext, MainActivity::class.java)
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val name = "Channel"
        val descriptionText = "DescriptionChannel"
        val importance = NotificationManager.IMPORTANCE_LOW
        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            description = descriptionText
            lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
        }
        notificationManager.createNotificationChannel(channel)

        if (playbackstatus == Playbackstatus.PLAYING) {
            notificationAction = android.R.drawable.ic_media_pause
            playpauseAction = playbackAction(1)
            titleButton = "Play"
            notRemoveOnSwipe = true
        } else if (playbackstatus == Playbackstatus.PAUSED) {
            notificationAction = android.R.drawable.ic_media_play
            playpauseAction = playbackAction(0)
            titleButton = "Pause"
            notRemoveOnSwipe = false
        }

        val cancelDrawable = android.R.drawable.ic_menu_close_clear_cancel
        val largeIconDrawble = R.drawable.totemanimal_ivon
        val largeIcon: Bitmap = BitmapFactory.decodeResource(resources, largeIconDrawble)

        val notificationBuilder: NotificationCompat.Builder =
            NotificationCompat.Builder(this, CHANNEL_ID)
                .setShowWhen(false)
                .setStyle(
                    androidx.media.app.NotificationCompat.MediaStyle()
                        .setMediaSession(mediaSession!!.sessionToken)
                        .setShowActionsInCompactView(0, 1)
                )
                .setDefaults(0)
                .setColor(Color.GREEN)
                .setColorized(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setLargeIcon(largeIcon)
                .setSmallIcon(notificationAction)
                .setContentTitle(artist)
                .setContentText(song)
                .addAction(notificationAction, titleButton, playpauseAction)
                .addAction(cancelDrawable, "Cancel", playbackAction(10))
                .setOngoing(notRemoveOnSwipe)
                .setContentIntent(PendingIntent.getActivity(this, 0, contentIntent, PendingIntent.FLAG_UPDATE_CURRENT))

        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
        return notificationBuilder
    }

    fun startUpdateAlbumData(timeUntilUpdate: Long) {
        job?.cancel()
        job = CoroutineScope(Dispatchers.Main).launch {
            val okHttpClient = OkHttpClient.Builder().build()

            val retrofit: Retrofit = Retrofit.Builder()
                .baseUrl("https://stream.vedaradio.fm")
                .addConverterFactory(ScalarsConverterFactory.create())
                .build()
            val vedaradioService = retrofit.create(VedaradioRetrofitApi::class.java)
            vedaradioService.jsonPlease().enqueue(object : Callback<String> {
                override fun onResponse(
                    call: Call<String>,
                    response: Response<String>
                ) {
                    val gson = Gson()
                    val streamVedaradioJSONClass : StreamVedaradioJSONClass = gson.fromJson(response.body(), StreamVedaradioJSONClass::class.java)
                    streamVedaradioJSONClass?.icestats?.source?.get(0)?.title?.let {
                        val list = it.split("-")
                        when (list.size) {
                            1 -> {
                                artist = "Veda Radio"
                                song = list[0]
                            }
                            2 -> {
                                artist = list[0]
                                song = list[1]
                            }
                            3 -> {
                                artist = list[0]
                                song = list[1] + list[2]
                            }
                        }
                    }
                    when (STATE_OF_SERVICE) {
                        InitStatusMediaPlayer.PLAYING -> {
                            buildNotification(Playbackstatus.PLAYING)
                        }
                        InitStatusMediaPlayer.INIT_COMPLETE -> {
                            buildNotification(Playbackstatus.PAUSED)
                        }
                        else -> {
                            removeNotification()
                        }
                    }
                    Log.d("MyLogS", "update NowPlaying : $artist         $song")
                    handler?.postDelayed({ startUpdateAlbumData(timeUntilUpdate) }, timeUntilUpdate)
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    artist = "Veda radio"
                    song = "From Heart to Heart"
                    job?.cancel()

                }
            })
        }
    }

    private fun playbackAction(actionNumber: Int): PendingIntent? {
        val playbackAction = Intent(this, RadioPlayerService::class.java)
        when (actionNumber) {
            10 -> {
                playbackAction.action = ACTION_CANCEL
                return PendingIntent.getService(this, actionNumber, playbackAction, 0)
            }
            0 -> {
                playbackAction.action = ACTION_PLAY
                return PendingIntent.getService(this, actionNumber, playbackAction, 0)
            }
            1 -> {
                playbackAction.action = ACTION_PAUSE
                return PendingIntent.getService(this, actionNumber, playbackAction, 0)
            }
        }
        return null
    }

    private fun handleIncomingAction(playbackAction: Intent) {
        if (playbackAction.action == null) return
        when (playbackAction.action!!) {
            ACTION_PLAY -> transportControls?.play()
            ACTION_PAUSE -> transportControls?.pause()
            ACTION_CANCEL -> transportControls?.stop()
        }
    }

    private fun removeNotification() {
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(NOTIFICATION_ID)
    }

    override fun onCompletion(mp: MediaPlayer?) {
        stopMedia()
        stopForeground(true)
        stopSelf()
    }

    override fun onPrepared(mp: MediaPlayer?) {
        if (mediaPlayer == null) return
        STATE_OF_SERVICE = InitStatusMediaPlayer.INIT_COMPLETE
        playMedia()
    }

    override fun onError(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
        STATE_OF_SERVICE = InitStatusMediaPlayer.IDLE
        when (what) {
            MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK -> {
                Log.d("MyLogS", "error : not valid progressive playback $what  $extra")
                Toast.makeText(this, "Неверный формат аудио", Toast.LENGTH_SHORT).show()
                stopForeground(true)
                stopSelf()
            }
            MediaPlayer.MEDIA_ERROR_SERVER_DIED -> {
                Log.d("MyLogS", "error : server died $what  $extra")
                Toast.makeText(this, "Сервер недоступен", Toast.LENGTH_SHORT).show()
                stopForeground(true)
                stopSelf()
            }
            MediaPlayer.MEDIA_ERROR_UNKNOWN -> {
                Log.d("MyLogS", "error : unknow $what $extra")
                mediaPlayer = null
                Toast.makeText(this, "Неизвестная ошибка", Toast.LENGTH_SHORT).show()
                stopForeground(true)
                stopSelf()
            }
            MediaPlayer.MEDIA_ERROR_TIMED_OUT -> {
                Log.d("MyLogS", "error : time out $what  $extra")
                mediaPlayer = null
                Toast.makeText(this, "Превышено время ожидания ответа сервера", Toast.LENGTH_SHORT).show()
                stopForeground(true)
                stopSelf()
            }
            else -> {
                Log.d("MyLogS", "error : $what  $extra")
                mediaPlayer = null
                Toast.makeText(this, "Ошибка загрузки", Toast.LENGTH_SHORT).show()
                stopForeground(true)
                stopSelf()
            }
        }
        return false
    }

    override fun onSeekComplete(mp: MediaPlayer?) {
    }

    override fun onInfo(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
        return false
    }

    override fun onBufferingUpdate(mp: MediaPlayer?, percent: Int) {
    }

    override fun onAudioFocusChange(focusChange: Int) {
        when (focusChange) {
            AudioManager.AUDIOFOCUS_GAIN -> {
                if (mediaPlayer == null) {
                    initMediaPlayer()
                } else if (STATE_OF_SERVICE == InitStatusMediaPlayer.INIT_COMPLETE) {
                    playMedia()
                    mediaPlayer!!.setVolume(1.0f, 1.0f)
                }
            }
            AudioManager.AUDIOFOCUS_LOSS -> {
                if (STATE_OF_SERVICE == InitStatusMediaPlayer.PLAYING) pauseMedia()
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                if (STATE_OF_SERVICE == InitStatusMediaPlayer.PLAYING) pauseMedia()
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                if (STATE_OF_SERVICE == InitStatusMediaPlayer.PLAYING) mediaPlayer?.setVolume(0.1f, 0.1f)
            }
        }
    }

    private fun requestAudioFocus(): Boolean {
        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager?
        val result = audioManager?.requestAudioFocus(
            this,
            AudioManager.STREAM_MUSIC,
            AudioManager.AUDIOFOCUS_GAIN
        )
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            return true
        }
        return false
    }

    private fun removeAudioFocus() = AudioManager.AUDIOFOCUS_REQUEST_GRANTED == audioManager?.abandonAudioFocus(this)

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val firstRun = intent?.extras?.getBoolean(TAG_FIRST_RUN) ?: false
        Log.d("MyLogS", "onStartCommand: intent firstRun: $firstRun")
        intent?.extras?.getString(TAG_NEW_AUDIO_URL).let {
            if (it != null && it != "") urlString = it
            Log.d("MyLogS", "onStartCommand: intent URL: $it")
        }
        if (!requestAudioFocus()) {
            stopSelf()
        }
        if (mediaSessionManager == null) {
            try {
                initMediaSession()
                initMediaPlayer()
                if (firstRun) {
                    startForeground(NOTIFICATION_ID, buildNotification(Playbackstatus.PAUSED).build())
                    startUpdateAlbumData(60000)
                }
            } catch (e: RemoteException) {
                e.printStackTrace()
                stopSelf()
            }
        }
        if (intent != null) {
            handleIncomingAction(intent)
        }
        return START_STICKY
    }

//----------------------------MediaPlayerControl-------------------------------------------

    fun playMedia() {
        if (mediaPlayer == null) return
        if (STATE_OF_SERVICE == InitStatusMediaPlayer.INIT_COMPLETE) {
            mediaPlayer?.start()
            STATE_OF_SERVICE = InitStatusMediaPlayer.PLAYING
            buildNotification(Playbackstatus.PLAYING)
            updateMetaData()
        }
    }

    fun stopMedia() {
        STATE_OF_SERVICE = InitStatusMediaPlayer.IDLE
        if (mediaPlayer == null) return
        mediaPlayer?.stop()
        removeNotification()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    fun pauseMedia() {
        if (mediaPlayer == null) return
        if (STATE_OF_SERVICE == InitStatusMediaPlayer.PLAYING) {
            mediaPlayer?.pause()
            resumePosition = mediaPlayer!!.currentPosition
            STATE_OF_SERVICE = InitStatusMediaPlayer.INIT_COMPLETE
            buildNotification(Playbackstatus.PAUSED)
        }
    }

    fun getStatusMediaMplayer(): InitStatusMediaPlayer = STATE_OF_SERVICE

    fun getPlayingURL(): String? = urlString

//------------------------------------------------------------------------

    private val broadcastReceiverNewAudio = object : BroadcastReceiverForPlayerService() {
        override fun onReceive(context: Context?, intent: Intent?) {
            urlString = intent?.getStringExtra(TAG_NEW_AUDIO_URL)
            stopMedia()
            mediaPlayer?.reset()
            initMediaPlayer()
        }
    }

    private fun broadcastTellNewStatus() {
        sendBroadcast(Intent(Broadcast_STATE_SERVICE).putExtra(TAG_STATE_SERVICE, STATE_OF_SERVICE))
    }

    private fun registerPlayNewAudio() {
        registerReceiver(broadcastReceiverNewAudio, IntentFilter(Broadcast_NEW_AUDIO))
    }

    private fun callStateListener() {
        telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        phoneStateListener = object : PhoneStateListener() {
            override fun onCallStateChanged(state: Int, phoneNumber: String?) {
                when (state) {
                    TelephonyManager.CALL_STATE_OFFHOOK -> {
                    }
                    TelephonyManager.CALL_STATE_RINGING -> {
                        if (mediaPlayer != null) {
                            pauseMedia()
                            onIncomingCall = true
                        }
                    }
                    TelephonyManager.CALL_STATE_IDLE -> {
                        if (mediaPlayer != null) {
                            if (onIncomingCall) {
                                onIncomingCall = false
                                playMedia()
                            }
                        }
                    }
                }
            }
        }
        telephonyManager?.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE)
    }


    override fun onTaskRemoved(rootIntent: Intent?) {
        Log.d("MyLogS", "OnTaskRemove : ${rootIntent?.action}")
        super.onTaskRemoved(rootIntent)
    }

    override fun onCreate() {
        super.onCreate()
        callStateListener()
        registerPlayNewAudio()
        val powerManager = getSystemService(POWER_SERVICE) as PowerManager
        val wakeLock: WakeLock = powerManager.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK,
            "RadioVEDA::MyWakelockTag"
        )
        wakeLock.acquire(30*60*1000L /*30 minutes*/)
    }

    override fun onDestroy() {
        Log.d("MyLogS","onDestroyService")
        removeNotification()
        STATE_OF_SERVICE = InitStatusMediaPlayer.IDLE
        handler = null
        if (mediaPlayer != null) {
            stopMedia()
            mediaPlayer?.release()
        }
        removeAudioFocus()
        if (phoneStateListener != null) {
            telephonyManager?.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE)
        }
        unregisterReceiver(broadcastReceiverNewAudio)
        super.onDestroy()
    }

}