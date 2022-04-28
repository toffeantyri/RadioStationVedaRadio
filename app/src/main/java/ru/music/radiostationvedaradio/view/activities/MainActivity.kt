package ru.music.radiostationvedaradio.view.activities

import android.media.AudioManager
import android.os.Bundle
import android.util.Log
import android.widget.ExpandableListAdapter
import kotlinx.android.synthetic.main.activity_main.*
import ru.music.radiostationvedaradio.R
import ru.music.radiostationvedaradio.view.adapters.expandableList.ExpandableListAdapterForNavView

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

        setupExpandableListFromNavView()
        setupListViewFromNavView()

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