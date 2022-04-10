package ru.music.radiostationvedaradio

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : FragmentActivity() {

    lateinit var myMediaPlayer: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        myMediaPlayer = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA).build()
            )
            setDataSource(getString(R.string.veda_radio_stream_link))
            prepare()
        }




        btn_start.setOnClickListener {
            myMediaPlayer.start()
        }

        btn_stop.setOnClickListener {
            myMediaPlayer.pause()
        }
    }

    override fun onStop() {
        super.onStop()
        myMediaPlayer.pause()
    }

    override fun onResume() {
        super.onResume()

    }
}