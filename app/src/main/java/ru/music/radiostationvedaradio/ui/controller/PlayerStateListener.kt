package ru.music.radiostationvedaradio.ui.controller

import android.media.session.PlaybackState
import android.util.Log
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import kotlinx.coroutines.flow.MutableStateFlow
import ru.music.radiostationvedaradio.utils.LOG_TAG

class PlayerStateListener(
    controller: MediaController
) : Player.Listener {

    private val _playStateFlow: MutableStateFlow<Boolean> = MutableStateFlow(controller.isPlaying)
    val playStateFlow: MutableStateFlow<Boolean> get() = _playStateFlow

    override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
        Log.d(LOG_TAG, "onMediaMetadataChanged ${mediaMetadata.title}")
        super.onMediaMetadataChanged(mediaMetadata)
    }

    override fun onPlaybackStateChanged(playbackState: Int) {
        if (playbackState == PlaybackState.STATE_PAUSED) {
            playStateFlow.tryEmit(false)
        }
        super.onPlaybackStateChanged(playbackState)
    }

    override fun onIsPlayingChanged(isPlaying: Boolean) {
        playStateFlow.tryEmit(isPlaying)
        super.onIsPlayingChanged(isPlaying)
    }


}