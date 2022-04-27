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
        dataModel.statusFragmentConnected.observe(this){
            fragmentIsConnected = it
        }

        myDrawerLayout = drawer_menu
        expandableList = exp_list_nav_menu
        navigationView = draw_navView
        if(navigationView != null){
            setupDrawerContent(navigationView)
        }
        prepareListData()

        mMenuAdapter = ExpandableListAdapterForNavView(this, listDataHeader, listDataChild, expandableList)
        expandableList.setAdapter(mMenuAdapter)

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