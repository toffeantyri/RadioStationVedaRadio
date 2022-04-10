package ru.music.radiostationvedaradio

import android.media.AudioAttributes
import android.media.MediaPlayer

class RadioClass(urlStream: String) {

    private var urlRadioStream: String = urlStream
    private val myMediaPlayer = MediaPlayer().apply {
        setAudioAttributes(
            AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setUsage(AudioAttributes.USAGE_MEDIA).build()
        )
        setDataSource(urlRadioStream)
        prepare()
    }

    fun myPlay() {
        myMediaPlayer.start()
    }

    fun myPause() {
        myMediaPlayer.pause()
    }

    fun isPlaying()= myMediaPlayer.isPlaying



}
