package ru.music.radiostationvedaradio.activityes

import android.Manifest
import android.media.AudioManager
import android.os.Bundle
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
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
            getString(R.string.veda_radio_stream_link_low)
        APP_CONTEXT = this


        initToolbar()
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

    override fun onResume() {
        super.onResume()
        volumeControlStream = AudioManager.STREAM_MUSIC
    }


    override fun onDestroy() {
        binding.mainBanner.destroy()
        if (serviceBound) {
            unbindService(serviceConnection)
        }
        APP_CONTEXT = null
        super.onDestroy()
    }


}