package ru.music.radiostationvedaradio.view.activities

import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.core.view.GravityCompat
import kotlinx.android.synthetic.main.activity_main.*
import ru.music.radiostationvedaradio.R
import ru.music.radiostationvedaradio.services.*

class MainActivity : BaseMainActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setUpActionBar()
        url = getString(R.string.veda_radio_stream_link_low) // TODO Качество по умолчанию на релиз - MEDIUM
        webUrl = getString(R.string.veda_radio_site)
        registerBroadcastStateService()
        playAudio(url)
        loadAndShowBanner()

        dataModel.statusFragmentConnected.observe(this){
            fragmentIsConnected = it
        }

    }

    override fun onPause() {
        super.onPause()
        Log.d("MyLog", "MainActivity onPause")
    }

    override fun onStart() {
        super.onStart()
        draw_navView.setUpDrawerNavViewListener()
        Log.d("MyLog", "MainActivity onStart")
    }

    override fun onResume() {
        super.onResume()
        volumeControlStream = AudioManager.STREAM_MUSIC
    }

    override fun onDestroy() {
        main_banner.destroy()
        if (serviceBound) {
            unbindService(serviceConnection)
        }
        super.onDestroy()
    }






}