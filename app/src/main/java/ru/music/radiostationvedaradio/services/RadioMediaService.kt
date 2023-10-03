package ru.music.radiostationvedaradio.services

import android.app.PendingIntent
import android.content.Intent
import androidx.media3.common.AudioAttributes
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.Log
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.google.common.util.concurrent.ListenableFuture
import ru.music.radiostationvedaradio.ui.MainActivity
import ru.music.radiostationvedaradio.utils.LOG_TAG

@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
class RadioMediaService : MediaSessionService(), MediaSession.Callback {

    private var mediaSession: MediaSession? = null
    private lateinit var player: Player
    override fun onConnect(
        session: MediaSession,
        controller: MediaSession.ControllerInfo
    ): MediaSession.ConnectionResult {
        Log.d(LOG_TAG, "service: onConnect")
        return super.onConnect(session, controller)
    }

    override fun onPostConnect(session: MediaSession, controller: MediaSession.ControllerInfo) {
        Log.d(LOG_TAG, "service: onPostConnect")
        super.onPostConnect(session, controller)
    }

    override fun onDisconnected(session: MediaSession, controller: MediaSession.ControllerInfo) {
        Log.d(LOG_TAG, "service: onDisconnected")
        super.onDisconnected(session, controller)
    }


    override fun onAddMediaItems(
        mediaSession: MediaSession,
        controller: MediaSession.ControllerInfo,
        mediaItems: MutableList<MediaItem>
    ): ListenableFuture<MutableList<MediaItem>> {
        mediaItems.forEach {
            Log.d(LOG_TAG, "service: mediaItem ${it.mediaId}")
        }

        return super.onAddMediaItems(mediaSession, controller, mediaItems)
    }


    override fun onCreate() {
        Log.d(LOG_TAG, "service: onCreate")
        super.onCreate()
        player = ExoPlayer.Builder(this)
            .setAudioAttributes(AudioAttributes.DEFAULT, true)
            .build()
        mediaSession = MediaSession.Builder(this, player)
            .setSessionActivity(getSingleTopActivity())
            .setCallback(this).build()
    }

    private fun getSingleTopActivity(): PendingIntent {
        return PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        Log.d(LOG_TAG, "service: onGetSession")
        return mediaSession
    }

    override fun onDestroy() {
        Log.d(LOG_TAG, "service: onDestroy")
        mediaSession?.run {
            player.release()
            this.release()
            mediaSession = null
        }
        super.onDestroy()
    }
}