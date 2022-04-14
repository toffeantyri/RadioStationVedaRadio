package ru.music.radiostationvedaradio.services

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
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
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import android.util.Log
import androidx.core.app.NotificationCompat
import ru.music.radiostationvedaradio.R
import ru.music.radiostationvedaradio.view.Broadcast_NEW_AUDIO
import ru.music.radiostationvedaradio.view.MainActivity
import ru.music.radiostationvedaradio.viewmodel.ViewModelMainActivity

const val ACTION_PLAY = "ru.music.vedaradio.ACTION_PLAY"
const val ACTION_PAUSE = "ru.music.vedaradio.ACTION_PAUSE"
const val ACTION_CANCEL = "ru.music.vedaradio.ACTION_CANCEL"

const val CHANNEL_ID = "ru.music.vedaradio.ID"

const val NOTIFICATION_ID = 101

// -1 - неинициализирован, 0 - инициализируется, 1 - инициализирован
var STATE_INIT_MEDIAPLAYER = InitStatusMediaPlayer.IDLE

class RadioPlayerService : Service(), MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener,
    MediaPlayer.OnErrorListener, MediaPlayer.OnSeekCompleteListener, MediaPlayer.OnInfoListener,
    MediaPlayer.OnBufferingUpdateListener, AudioManager.OnAudioFocusChangeListener {

    private val iBinder: IBinder = LocalBinder()

    var urlString: String? = null

    private var resumePosition: Int = 0
    private var mediaPlayer: MediaPlayer? = null
    private var audioManager: AudioManager? = null
    lateinit var dataModelInner: ViewModelMainActivity
    private var onIncomingCall = false
    private var phoneStateListener: PhoneStateListener? = null
    private var telephonyManager: TelephonyManager? = null

    private var mediaSessionManager: MediaSessionManager? = null
    private var mediaSession: MediaSessionCompat? = null
    private var transportControls: MediaControllerCompat.TransportControls? = null

    private fun initMediaPlayer() {
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
        STATE_INIT_MEDIAPLAYER = InitStatusMediaPlayer.INITIALISATION
        mediaPlayer?.prepareAsync()
        Log.d("MyLog", "initMEdiaPlayer")
    }

    private fun initMediaSession() {
        if (mediaSessionManager != null) return
        mediaSessionManager = getSystemService(Context.MEDIA_SESSION_SERVICE) as MediaSessionManager
        mediaSession = MediaSessionCompat(applicationContext, "Audio Player")
        transportControls = mediaSession?.controller?.transportControls
        mediaSession?.isActive = true
        mediaSession?.setFlags(MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS)
        updateMetaData()

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
                removeNotification()
                stopSelf()
                buildNotification(Playbackstatus.STOPPED)
            }

            override fun onSeekTo(pos: Long) {
                super.onSeekTo(pos)
            }
        })
        Log.d("MyLog", "initMediaSession")
    }

    private fun updateMetaData() {
        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_baseline_radio_24)
        //todo bitmap
        mediaSession?.setMetadata(
            MediaMetadataCompat.Builder()
                .putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, bitmap)
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, "Artist")
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, "ALBUM")
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, "Title").build()


        )
    }

    private fun buildNotification(playbackstatus: Playbackstatus) {
        var notificationAction = android.R.drawable.ic_media_pause
        var playPause_action: PendingIntent? = null
        var titleButton: String = ""

        if (playbackstatus == Playbackstatus.PLAYING) {
            notificationAction = android.R.drawable.ic_media_pause
            playPause_action = playbackAction(1)
            titleButton = "Play"
        } else if (playbackstatus == Playbackstatus.PAUSED) {
            notificationAction = android.R.drawable.ic_media_play
            playPause_action = playbackAction(0)
            titleButton = "Pause"
        }

        val cancelDrawable = android.R.drawable.ic_menu_close_clear_cancel
        val largeIconDrawble = R.drawable.totemanimal_ivon
        val largeIcon: Bitmap = BitmapFactory.decodeResource(resources, largeIconDrawble)

        val notificationBuilder: NotificationCompat.Builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setDefaults(0)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setShowWhen(false)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setLargeIcon(largeIcon)
            .setSmallIcon(notificationAction)
            .setContentText("N:Artist")
            .setContentTitle("N:Album")
            .setContentInfo("N:Title")
            .addAction(notificationAction, titleButton, playPause_action)
            .addAction(cancelDrawable, "Cancel", playbackAction(-1))
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setMediaSession(mediaSession?.sessionToken)
                    .setShowActionsInCompactView(0, 1)
            )
            .setOngoing(true)
            .setColor(Color.GREEN)
            .setColorized(true)


        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val name = "Channel 1"
        val descriptionText = "Description 1"
        val importance = NotificationManager.IMPORTANCE_LOW
        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            description = descriptionText
            lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
        }
        notificationManager.createNotificationChannel(channel)
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
    }

    private fun playbackAction(actionNumber: Int): PendingIntent? {
        val playbackAction = Intent(this, RadioPlayerService::class.java)
        when (actionNumber) {
            -1 -> {
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
        val actionString: String = playbackAction.action!!
        if (actionString == ACTION_PLAY) {
            playMedia()
        } else if (actionString == ACTION_PAUSE) {
            pauseMedia()
        } else if (actionString == ACTION_CANCEL) {
            removeNotifityMedia()
        }
    }

    private fun removeNotification() {
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(NOTIFICATION_ID)
    }

    override fun onBind(intent: Intent): IBinder {
        return iBinder
    }

    override fun onCompletion(mp: MediaPlayer?) {
        stopMedia()
        stopSelf()
        dataModelInner.preparedStateComplete.value = false
        Log.d("MyLog", "onComplition: ${dataModelInner.preparedStateComplete.value}")
    }

    override fun onPrepared(mp: MediaPlayer?) {
        if (mediaPlayer == null) return
        if (mediaPlayer != null) {
            dataModelInner.preparedStateComplete.value = true
            Log.d("MyLog", "onPrepader: ${dataModelInner.preparedStateComplete.value}")
        }
        STATE_INIT_MEDIAPLAYER = InitStatusMediaPlayer.INIT_COMPLETE
        playMedia()
        Log.d("MyLog", "Service ready for play")
    }

    override fun onError(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
        dataModelInner.preparedStateComplete.value = false
        Log.d("MyLog", "onError: ${dataModelInner.preparedStateComplete.value}")
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
                } else if (!mediaPlayer!!.isPlaying) {
                    playMedia()
                    mediaPlayer!!.setVolume(1.0f, 1.0f)
                }
            }
            AudioManager.AUDIOFOCUS_LOSS -> {
                if (mediaPlayer!!.isPlaying) {
                    mediaPlayer?.let {
                        stopMedia()

                    }
                    mediaPlayer?.release()
                    STATE_INIT_MEDIAPLAYER = InitStatusMediaPlayer.IDLE
                    mediaPlayer = null
                }
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                if (mediaPlayer!!.isPlaying) {
                    pauseMedia()
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
        Log.d("MyLog, ", "onStartCommand requestAudioFocus: ${requestAudioFocus()}")
        if (!requestAudioFocus()) {
            stopSelf()
        }
        if (mediaSessionManager == null) {
            try {
                initMediaSession()
                if (STATE_INIT_MEDIAPLAYER == InitStatusMediaPlayer.IDLE) {
                    initMediaPlayer()
                }
            } catch (e: RemoteException) {
                e.printStackTrace()
                stopSelf()
            }
            buildNotification(Playbackstatus.PAUSED)
        }
        if (intent != null) handleIncomingAction(intent)

        return super.onStartCommand(intent, flags, startId)
    }

//----------------------------MediaPlayerControl-------------------------------------------

    fun playMedia() {
        if (mediaPlayer == null) return
        if (!mediaPlayer!!.isPlaying) {
            mediaPlayer!!.start()
            dataModelInner.stateIsPlaying.value = mediaPlayer!!.isPlaying
            transportControls?.play()
        }
    }

    fun stopMedia() {
        if (mediaPlayer == null) return
        mediaPlayer?.stop()
        STATE_INIT_MEDIAPLAYER = InitStatusMediaPlayer.IDLE
        dataModelInner.stateIsPlaying.value = mediaPlayer!!.isPlaying
        dataModelInner.preparedStateComplete.value = false
        Log.d("MyLog", "stopMedia: ${dataModelInner.preparedStateComplete.value}")
    }

    fun pauseMedia() {
        if (mediaPlayer == null) return
        if (mediaPlayer!!.isPlaying) {
            mediaPlayer!!.pause()
            resumePosition = mediaPlayer!!.currentPosition
            dataModelInner.stateIsPlaying.value = mediaPlayer!!.isPlaying
            transportControls?.pause()
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

    fun removeNotifityMedia() {
        pauseMedia()
        removeNotification()
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
            Log.d("MyLog", "onReceive 1 broadcastReseiver")
            pauseMedia()
            buildNotification(Playbackstatus.PAUSED)
        }
    }

    private val broadcastReceiverNewAudio = object : BroadcastReceiverForPlayerSevice() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.d("MyLog", "onReceive 2 broadcastReseiver")
            if (STATE_INIT_MEDIAPLAYER == InitStatusMediaPlayer.INITIALISATION) return

            stopMedia()
            mediaPlayer?.reset()
            initMediaPlayer()
            updateMetaData()
            buildNotification(Playbackstatus.PLAYING)

        }
    }

    private fun registerPlayNewAudio() {
        val filter = IntentFilter(Broadcast_NEW_AUDIO)
        registerReceiver(broadcastReceiverNewAudio, filter)
    }

    private fun registerBroadcastListener() {
        val intentFilter: IntentFilter = IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY)
        registerReceiver(broadcastReceiver, intentFilter)
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

    override fun onCreate() {
        super.onCreate()
        callStateListener()
        registerBroadcastListener()
        registerPlayNewAudio()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mediaPlayer != null) {
            stopMedia()
            mediaPlayer!!.release()
        }
        STATE_INIT_MEDIAPLAYER = InitStatusMediaPlayer.IDLE
        dataModelInner.preparedStateComplete.value = false
        Log.d("MyLog", "onDestroy: ${dataModelInner.preparedStateComplete.value}")
        removeAudioFocus()
        if (phoneStateListener != null) {
            telephonyManager?.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE)
        }
        removeNotification()
        unregisterReceiver(broadcastReceiver)
        unregisterReceiver(broadcastReceiverNewAudio)
    }

    inner class LocalBinder : Binder() {
        fun getService(dataModel: ViewModelMainActivity): RadioPlayerService {
            dataModelInner = dataModel
            return this@RadioPlayerService
        }
    }
}