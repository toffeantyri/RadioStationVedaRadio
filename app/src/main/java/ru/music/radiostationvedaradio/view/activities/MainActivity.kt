package ru.music.radiostationvedaradio.view.activities

import android.media.AudioManager
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import ru.music.radiostationvedaradio.R
import ru.music.radiostationvedaradio.view.activities.BaseMainActivity

class MainActivity : BaseMainActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setUpActionBar()
        url = getString(R.string.veda_radio_stream_link_low) // TODO Качество по умолчанию на релиз - MEDIUM
        registerBroadcastStateService()
        playAudio(url)
        loadAndShowBanner()

        draw_navView.setNavigationItemSelectedListener {
            when(it.itemId){
                R.id.item1 -> {

                }
            }
            return@setNavigationItemSelectedListener true
        }
    }

    override fun onPause() {
        super.onPause()
        Log.d("MyLog", "MainActivity onPause")
    }

    override fun onStart() {
        super.onStart()
        setUpOnItemClickDrawerMenu()
        Log.d("MyLog", "MainActivity onStart")
    }

    override fun onResume() {
        super.onResume()
        volumeControlStream = AudioManager.STREAM_MUSIC


    }

    override fun onDestroy() {
        if (serviceBound) {
            unbindService(serviceConnection)
        }
        super.onDestroy()
    }







}