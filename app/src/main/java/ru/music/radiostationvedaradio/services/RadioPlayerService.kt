package ru.music.radiostationvedaradio.services

import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.ViewModel
import ru.music.radiostationvedaradio.viewmodel.ViewModelMainActivity


class RadioPlayerService : Service(), MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener,
    MediaPlayer.OnErrorListener, MediaPlayer.OnSeekCompleteListener, MediaPlayer.OnInfoListener,
    MediaPlayer.OnBufferingUpdateListener, AudioManager.OnAudioFocusChangeListener {
    val iBinder: IBinder = LocalBinder()

    var urlString: String? = null
    private var resumePosition: Int = 0
    private var mediaPlayer: MediaPlayer? = null
    private var audioManager: AudioManager? = null
    lateinit var dataModelInner : ViewModelMainActivity

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
        if(mediaPlayer != null) dataModelInner.preparedStateComplete.value = true
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
        if (urlString != null && urlString != "") {
            initMediaPlayer()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    inner class LocalBinder : Binder() {
        fun getService(dataModel: ViewModelMainActivity): RadioPlayerService {
            dataModelInner = dataModel
            return  this@RadioPlayerService
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mediaPlayer != null) {
            stopMedia()
            mediaPlayer!!.release()
        }
        dataModelInner.preparedStateComplete.value = false
        removeAudioFocus()
    }

}