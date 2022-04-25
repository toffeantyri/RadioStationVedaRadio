package ru.music.radiostationvedaradio.view.activities

import android.content.DialogInterface
import android.content.Intent
import android.media.AudioManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*
import ru.music.radiostationvedaradio.R
import ru.music.radiostationvedaradio.view.activities.BaseMainActivity
import ru.music.radiostationvedaradio.view.fragments.WebViewFragment

class MainActivity : BaseMainActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setUpActionBar()
        url = getString(R.string.veda_radio_stream_link_low) // TODO Качество по умолчанию на релиз - MEDIUM
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
        setUpDrawerNavViewListener()
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