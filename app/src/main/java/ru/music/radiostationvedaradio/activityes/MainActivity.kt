package ru.music.radiostationvedaradio.activityes

import android.media.AudioManager
import android.os.Bundle
import android.util.Log
import androidx.navigation.Navigation
import com.yandex.mobile.ads.banner.BannerAdView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import ru.music.radiostationvedaradio.R
import ru.music.radiostationvedaradio.utils.APP_CONTEXT
import ru.music.radiostationvedaradio.utils.myLog


class MainActivity : BaseMainActivity() {

    private var job: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        myLog("MainActivity onCreate")
        setContentView(R.layout.activity_main)
        urlRadioService = getString(R.string.veda_radio_stream_link_low) // TODO Качество по умолчанию на релиз - MEDIUM
        APP_CONTEXT = this


        job = CoroutineScope(Dispatchers.Main).launch {
            Log.d("MyLog", "Coroutine job : $job")
            setUpToolBar()
            initPlayerPanel()
            navController = Navigation.findNavController(this@MainActivity, R.id.main_nav_host_fragment)
            initExpandableListInNavView()
            initListViewOfNavMenuListener()
            registerBroadcastStateService()
            registerBroadcastNewSongService()
            loadAndShowBanner()
        }



        playAudio(urlRadioService)

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

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        findViewById<BannerAdView>(R.id.main_banner).destroy()
        if (serviceBound) {
            unbindService(serviceConnection)
        }
        APP_CONTEXT = null
        super.onDestroy()
    }


}