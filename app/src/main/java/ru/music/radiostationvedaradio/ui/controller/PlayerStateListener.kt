package ru.music.radiostationvedaradio.ui.controller

import android.media.session.PlaybackState
import android.util.Log
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import kotlinx.coroutines.flow.MutableStateFlow
import ru.music.radiostationvedaradio.utils.LOG_TAG

class PlayerStateListener(
    private val controller: MediaController
) : Player.Listener {

    val playStateFlow: MutableStateFlow<Boolean> = MutableStateFlow(controller.isPlaying)

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