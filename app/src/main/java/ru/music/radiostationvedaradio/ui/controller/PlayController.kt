package ru.music.radiostationvedaradio.ui.controller

import android.media.session.PlaybackState
import android.net.Uri
import android.util.Log
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import kotlinx.coroutines.flow.MutableStateFlow
import ru.music.radiostationvedaradio.services.LOG_TAG

class PlayController(
    private val controller: MediaController
) : Player.Listener {

    val playStateFlow: MutableStateFlow<Boolean> = MutableStateFlow(false)
    private var url: String = ""
    fun pushUrl(urlStream: String) {
        if (urlStream != url) {
            url = urlStream
            controller.apply {
                val uri = Uri.parse(urlStream)
                val newItem = MediaItem.Builder().setMediaId(urlStream).setUri(uri).build()
                setMediaItem(newItem)
                prepare()
                controller.playWhenReady = true
            }
        }
    }

    fun play() {
        if (!controller.isPlaying) {
            controller.play()
        }
    }

    fun pause() {
        if (controller.isPlaying) {
            controller.playWhenReady = false
            controller.pause()
        }
    }

    override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
        Log.d(LOG_TAG, "onMediaMetadataChanged ${mediaMetadata.title}")
        super.onMediaMetadataChanged(mediaMetadata)
    }

    override fun onPlaybackStateChanged(playbackState: Int) {
        Log.d(LOG_TAG, "onPlaybackStateChanged $playbackState")
        if (playbackState == PlaybackState.STATE_PAUSED) {
            playStateFlow.tryEmit(false)
        }
        super.onPlaybackStateChanged(playbackState)
    }

    override fun onIsPlayingChanged(isPlaying: Boolean) {
        Log.d(LOG_TAG, "onIsPlayingChanged $isPlaying")
        playStateFlow.tryEmit(isPlaying)
        super.onIsPlayingChanged(isPlaying)
    }


}