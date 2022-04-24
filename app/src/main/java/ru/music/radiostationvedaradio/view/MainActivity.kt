package ru.music.radiostationvedaradio.view

import android.media.AudioManager
import android.os.Bundle
import android.util.Log
import ru.music.radiostationvedaradio.R

class MainActivity : BaseMainActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setUpActionBar()
        setUpOnItemClickDrawerMenu()
        url = getString(R.string.veda_radio_stream_link_low)
        registerBroadcastStateService()
        playAudio(url)
    }

    override fun onPause() {
        super.onPause()
        Log.d("MyLog", "MainActivity onPause")
    }

    override fun onResume() {
        super.onResume()
        volumeControlStream = AudioManager.STREAM_MUSIC
        Log.d("MyLog", "MainActivity onResume")

    }

    override fun onDestroy() {
        if (serviceBound) {
            unbindService(serviceConnection)
        }
        super.onDestroy()
    }







}