package ru.music.radiostationvedaradio.services

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaMetadata
import android.media.MediaPlayer
import android.media.session.MediaController
import android.media.session.MediaSession
import android.media.session.MediaSessionManager
import android.os.Binder
import android.os.IBinder
import android.os.RemoteException
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import android.util.Log
import androidx.core.app.NotificationCompat
import ru.music.radiostationvedaradio.BroadcastReceiverForPlayerSevice
import ru.music.radiostationvedaradio.Playbackstatus
import ru.music.radiostationvedaradio.R
import ru.music.radiostationvedaradio.viewmodel.ViewModelMainActivity

const val ACTION_PLAY = "ru.music.vedaradio.ACTION_PLAY"
const val ACTION_PAUSE = "ru.music.vedaradio.ACTION_PAUSE"

const val CHANNEL_ID = "777"

const val NOTIFICATION_ID = 101

class RadioPlayerService : Service(), MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener,
    MediaPlayer.OnErrorListener, MediaPlayer.OnSeekCompleteListener, MediaPlayer.OnInfoListener,
    MediaPlayer.OnBufferingUpdateListener, AudioManager.OnAudioFocusChangeListener {

    val iBinder: IBinder = LocalBinder()

    var urlString: String? = null

    private var resumePosition: Int = 0
    private var mediaPlayer: MediaPlayer? = null
    private var audioManager: AudioManager? = null
    lateinit var dataModelInner: ViewModelMainActivity
    private var onIncomingCall = false
    private var phoneStateListener: PhoneStateListener? = null
    private var telephonyManager: TelephonyManager? = null

    private var mediaSessionManager: MediaSessionManager? = null
    private var mediaSession: MediaSession? = null
    private var transportControls: MediaController.TransportControls? = null

    fun initMediaPlayer() {
        mediaPlayer = MediaPlayer()
        mediaPlayer?.apply {
            setOnCompletionListener(this@RadioPlayerService)
            setOnErrorListener(this@RadioPlayerService)
            setOnPreparedListener(this@RadioPlayerService)
            setOnBufferingUpdateListener(this@RadioPlayerService)
            setOnSeekCompleteListener(this@RadioPlayerService)
            setOnInfoListener(this@RadioPlayerService)
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
        mediaSession = MediaSession(applicationContext, "Audio Player")
        transportControls = mediaSession?.controller?.transportControls
        mediaSession?.isActive = true
        mediaSession?.setFlags(MediaSession.FLAG_HANDLES_TRANSPORT_CONTROLS)
        updateMetaData()

        mediaSession?.setCallback(object : MediaSession.Callback() {
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
                removeNotification()
                stopSelf()
            }

            override fun onSeekTo(pos: Long) {
                super.onSeekTo(pos)
            }
        })
    }

    private fun updateMetaData() {
        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_baseline_radio_24)
        //todo bitmap
        mediaSession?.setMetadata(
            MediaMetadata.Builder()
                .putBitmap(MediaMetadata.METADATA_KEY_ALBUM_ART, bitmap)
                .putString(MediaMetadata.METADATA_KEY_ARTIST, "Artist")
                .putString(MediaMetadata.METADATA_KEY_ALBUM, "ALBUM")
                .putString(MediaMetadata.METADATA_KEY_TITLE, "Title").build()


        )
    }

    private fun buildNotification(playbackstatus: Playbackstatus) {
        var notificationAction = android.R.drawable.ic_media_pause
        var playPause_action: PendingIntent? = null

        if (playbackstatus == Playbackstatus.PLAYING) {
            notificationAction = android.R.drawable.ic_media_play
            playPause_action = playbackAction(1)
        } else if (playbackstatus == Playbackstatus.PAUSED) {
            notificationAction = android.R.drawable.ic_media_pause
            playPause_action = playbackAction(0)
        }

        val largeIconDrawble = R.drawable.totemanimal_ivon
        val largeIcon: Bitmap = BitmapFactory.decodeResource(resources, largeIconDrawble)

        val notificationBuilder: NotificationCompat.Builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setShowWhen(false)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setColor(resources.getColor(R.color.green_200))
            .setLargeIcon(largeIcon)
            .setSmallIcon(notificationAction)
            .setContentText("N:Artist")
            .setContentTitle("N:Album")
            .setContentInfo("N:Title")
            .addAction(notificationAction, "Pause", playbackAction(1))
            .addAction(notificationAction, "Play", playbackAction(0))
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val name = "Channel 1"
        val descriptionText = "Description 1"
        val importance = NotificationManager.IMPORTANCE_LOW
        val channel = NotificationChannel(CHANNEL_ID, name, importance ).apply{
            description = descriptionText
        }
        notificationManager.createNotificationChannel(channel)
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
    }

    private fun removeNotification() {
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(NOTIFICATION_ID)
    }

    private fun playbackAction(actionNumber: Int): PendingIntent? {
        val playbackAction = Intent(this, RadioPlayerService::class.java)
        when (actionNumber) {
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
        val actionString: String = playbackAction.action!!
        if (actionString == ACTION_PLAY) {
            transportControls?.play()
        } else if (actionString == ACTION_PAUSE) {
            transportControls?.pause()
        }
    }


    override fun onBind(intent: Intent): IBinder {
        return iBinder
    }

    override fun onCompletion(mp: MediaPlayer?) {
        stopMedia()
        stopSelf()
        dataModelInner.preparedStateComplete.value = false
    }

    override fun onPrepared(mp: MediaPlayer?) {
        if (mediaPlayer == null) return
        if (mediaPlayer != null) dataModelInner.preparedStateComplete.value = true
        Log.d("MyLog", "Service ready for play")
    }

    override fun onError(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
        dataModelInner.preparedStateComplete.value = false
        dataModelInner.stateIsPlaying.value = mediaPlayer?.isPlaying
        when (what) {
            MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK -> {
                Log.d("MyLog", "error : not valid progressive playback + $extra")
            }
            MediaPlayer.MEDIA_ERROR_SERVER_DIED -> {
                Log.d("MyLog", "error : server died + $extra")
            }
            MediaPlayer.MEDIA_ERROR_UNKNOWN -> {
                Log.d("MyLog", "error : unknow + $extra")
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
                    mediaPlayer!!.start()
                } else if (!mediaPlayer!!.isPlaying) {
                    mediaPlayer!!.start()
                    mediaPlayer!!.setVolume(1.0f, 1.0f)
                }
            }
            AudioManager.AUDIOFOCUS_LOSS -> {
                if (mediaPlayer!!.isPlaying) {
                    mediaPlayer?.let {
                        it.stop()
                        it.release()
                    }
                    mediaPlayer = null
                }
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                if (mediaPlayer!!.isPlaying) {
                    mediaPlayer!!.pause()
                }
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                if (mediaPlayer!!.isPlaying) {
                    mediaPlayer!!.setVolume(0.1f, 0.1f)
                }
            }
        }
    }

    private fun requestAudioFocus(): Boolean {
        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager?
        val result = audioManager?.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN)
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            return true
        }
        return false
    }

    private fun removeAudioFocus(): Boolean {
        return AudioManager.AUDIOFOCUS_REQUEST_GRANTED == audioManager?.abandonAudioFocus(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        try {
            urlString = intent?.extras?.getString("url")
        } catch (e: NullPointerException) {
            stopSelf()
        }
        if (!requestAudioFocus()) {
            stopSelf()
        }
        if (mediaSessionManager == null) {
            try {
                initMediaSession()
                initMediaPlayer()
            } catch (e: RemoteException) {
                e.printStackTrace()
                stopSelf()
            }
            buildNotification(Playbackstatus.PLAYING)
        }
//        if (intent != null)
            handleIncomingAction(intent!!)

        return super.onStartCommand(intent, flags, startId)
    }

//----------------------------MediaPlayerControl-------------------------------------------

    fun playMedia() {
        if (mediaPlayer == null) return
        if (!mediaPlayer!!.isPlaying) {
            mediaPlayer!!.start()
            dataModelInner.stateIsPlaying.value = mediaPlayer!!.isPlaying
        }
    }

    fun stopMedia() {
        if (mediaPlayer == null) return
        if (mediaPlayer!!.isPlaying) {
            mediaPlayer!!.stop()
            dataModelInner.stateIsPlaying.value = mediaPlayer!!.isPlaying
        }
    }

    fun pauseMedia() {
        if (mediaPlayer == null) return
        if (mediaPlayer!!.isPlaying) {
            mediaPlayer!!.pause()
            resumePosition = mediaPlayer!!.currentPosition
            dataModelInner.stateIsPlaying.value = mediaPlayer!!.isPlaying
        }
    }

    fun resumeMedia() {
        if (mediaPlayer == null) return
        if (!mediaPlayer!!.isPlaying) {
            mediaPlayer!!.seekTo(resumePosition)
            mediaPlayer!!.start()
            dataModelInner.stateIsPlaying.value = mediaPlayer!!.isPlaying
        }
    }

    fun reloadMedia() {
        dataModelInner.preparedStateComplete.value = false
        if (mediaPlayer == null) return
        if (mediaPlayer!!.isPlaying) {
            mediaPlayer!!.stop()
            dataModelInner.stateIsPlaying.value = isPlaying()
            mediaPlayer!!.release()
        }
        initMediaPlayer()
    }

    fun isPlaying(): Boolean {
        return when {
            mediaPlayer == null -> false
            !mediaPlayer!!.isPlaying -> false
            mediaPlayer!!.isPlaying -> true
            else -> false
        }
    }
//------------------------------------------------------------------------





    private val broadcastReceiver = object : BroadcastReceiverForPlayerSevice() {
        override fun onReceive(context: Context?, intent: Intent?) {
            pauseMedia()
            buildNotification(Playbackstatus.PAUSED)
        }
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

    fun registerBroadcatListener() {
        val intentFilter: IntentFilter = IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY)
        registerReceiver(broadcastReceiver, intentFilter)
    }

    override fun onCreate() {
        super.onCreate()
        callStateListener()
        registerBroadcatListener()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mediaPlayer != null) {
            stopMedia()
            mediaPlayer!!.release()
        }
        dataModelInner.preparedStateComplete.value = false
        removeAudioFocus()
        if (phoneStateListener != null) {
            telephonyManager?.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE)
        }
        removeNotification()
        unregisterReceiver(broadcastReceiver)
    }

    inner class LocalBinder : Binder() {
        fun getService(dataModel: ViewModelMainActivity): RadioPlayerService {
            dataModelInner = dataModel
            return this@RadioPlayerService
        }
    }
}