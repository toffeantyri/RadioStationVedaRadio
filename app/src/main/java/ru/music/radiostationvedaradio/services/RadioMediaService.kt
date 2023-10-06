package ru.music.radiostationvedaradio.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import androidx.core.app.NotificationCompat
import androidx.media3.common.AudioAttributes
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.Log
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.CommandButton
import androidx.media3.session.MediaNotification
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import androidx.media3.session.MediaStyleNotificationHelper
import com.google.common.collect.ImmutableList
import com.google.common.util.concurrent.ListenableFuture
import ru.music.radiostationvedaradio.R
import ru.music.radiostationvedaradio.ui.MainActivity
import ru.music.radiostationvedaradio.utils.LOG_TAG

@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
class RadioMediaService : MediaSessionService(), MediaSession.Callback {

    private var mediaSession: MediaSession? = null
    private lateinit var player: Player
    private lateinit var notificationBuilder: NotificationCompat.Builder
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
        this.setMediaNotificationProvider(mediaNotificationProvider)

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

    private val mediaNotificationProvider = object : MediaNotification.Provider {
        override fun createNotification(
            mediaSession: MediaSession,
            customLayout: ImmutableList<CommandButton>,
            actionFactory: MediaNotification.ActionFactory,
            onNotificationChangedCallback: MediaNotification.Provider.Callback
        ): MediaNotification {
            createCustomNotification(mediaSession)
            return MediaNotification(NOTIFICATION_ID, notificationBuilder.build())
        }

        override fun handleCustomCommand(
            session: MediaSession,
            action: String,
            extras: Bundle
        ): Boolean {
            return false
        }


    }

    override fun onUpdateNotification(session: MediaSession, startInForegroundRequired: Boolean) {

        super.onUpdateNotification(session, startInForegroundRequired)
    }

    private fun createCustomNotification(mediaSession: MediaSession) {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(
            NotificationChannel(
                CHANNEL_ID,
                NOTIFICATION_CHANNEL, NotificationManager.IMPORTANCE_LOW
            )
        )

        val notificationAction = android.R.drawable.ic_media_pause
        val largeIcon = BitmapFactory.decodeResource(resources, R.drawable.totemanimal_ivon)

        notificationBuilder =
            NotificationCompat.Builder(this, CHANNEL_ID)
                .setShowWhen(false)
                .setStyle(MediaStyleNotificationHelper.MediaStyle(mediaSession))
                .setColor(Color.GREEN)
                .setColorized(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setLargeIcon(largeIcon)
                .setSmallIcon(notificationAction)
                .setContentIntent(
                    PendingIntent.getActivity(
                        this,
                        0,
                        Intent(applicationContext, MainActivity::class.java),
                        PendingIntent.FLAG_MUTABLE
                    )
                )
    }

}