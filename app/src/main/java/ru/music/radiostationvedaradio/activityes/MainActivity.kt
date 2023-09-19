package ru.music.radiostationvedaradio.activityes

import android.Manifest
import android.media.AudioManager
import android.os.Bundle
import android.util.Log
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.yandex.mobile.ads.banner.BannerAdView
import ru.music.radiostationvedaradio.R
import ru.music.radiostationvedaradio.databinding.ActivityMainBinding
import ru.music.radiostationvedaradio.utils.APP_CONTEXT
import ru.music.radiostationvedaradio.utils.checkPermissionSingle


class MainActivity : BaseMainActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        urlRadioService =
            getString(R.string.veda_radio_stream_link_low) // TODO Качество по умолчанию на релиз - MEDIUM
        APP_CONTEXT = this


        initNavController()
        setUpToolBar()
        initPlayerPanel()
        initExpandableListInNavView()
        initListViewOfNavMenuListener()
        registerBroadcastStateService()
        registerBroadcastNewSongService()
        loadAndShowBanner()

        checkPermissionSingle(Manifest.permission.READ_PHONE_STATE) {
            playAudio(urlRadioService)
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