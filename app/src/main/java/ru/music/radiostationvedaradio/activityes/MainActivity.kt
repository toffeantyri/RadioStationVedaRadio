package ru.music.radiostationvedaradio.activityes

import android.Manifest
import android.media.AudioManager
import android.os.Bundle
import android.util.Log
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import ru.music.radiostationvedaradio.R
import ru.music.radiostationvedaradio.databinding.ActivityMainBinding
import ru.music.radiostationvedaradio.services.InitStatusMediaPlayer
import ru.music.radiostationvedaradio.utils.APP_CONTEXT
import ru.music.radiostationvedaradio.utils.TAG
import ru.music.radiostationvedaradio.utils.checkPermissionSingle
import ru.music.radiostationvedaradio.utils.showToast
import ru.music.radiostationvedaradio.view.adapters.OnFilterClickListener
import ru.music.radiostationvedaradio.view.adapters.filter_adapter.MenuArrayAdapter


class MainActivity : BaseMainActivity(), OnFilterClickListener {

    private val qualityAdapter by lazy {
        MenuArrayAdapter(
            this,
            this.resources.getStringArray(R.array.array_quality_list).toMutableList(),
            this
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        urlRadioService =
            getString(R.string.veda_radio_stream_link_low)
        APP_CONTEXT = this

        mediaListAdapter =
            FolderMediaItemArrayAdapter(this, R.layout.folder_items, subItemMediaList)
        initToolbar()
        initQualityChooser()
        initPlayerPanel()
        initExpandableListInNavView()
        initListViewOfNavMenuListener()
        registerBroadcastStateService()
        registerBroadcastNewSongService()
        loadAndShowBanner()

        checkPermissionSingle(Manifest.permission.READ_PHONE_STATE) {
            //playAudio(urlRadioService)
            startPlayerService(urlRadioService)
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


    private fun initQualityChooser() {
        with(binding.toolbarContainer) {
            qualityAdapter.setHeaderViewVisibility(false)
            qualityAdapter.setArrowViewVisibility(false)
            qualitySpinner.adapter = qualityAdapter
            qualitySpinner.setOnTouchListener(qualityAdapter.getUserSelectionClickListener())
            qualitySpinner.onItemSelectedListener = qualityAdapter.getUserSelectionClickListener()
        }
    }

    override fun onItemFilterClick(position: Int) {
        Log.d(TAG, "onItemFilterClick $position")
        when (position) {
            0 -> {
                setQualityAndPlay(getString(R.string.veda_radio_stream_link_low), position)
            }

            1 -> {
                setQualityAndPlay(getString(R.string.veda_radio_stream_link_medium), position)
            }

            2 -> {
                setQualityAndPlay(getString(R.string.veda_radio_stream_link_high), position)
            }
        }
    }

    private fun setQualityAndPlay(streamLink: String, position: Int) {
        Log.d(TAG, "setQualityAndPlay")
        if (viewModel.statusMediaPlayer.value == InitStatusMediaPlayer.INITIALISATION) {
            Log.d(TAG, "onItemFilterClick InitStatusMediaPlayer.INITIALISATION")
            this.showToast(getString(R.string.error_loading))
        } else {
            Log.d(TAG, "onItemFilterClick InitStatusMediaPlayer.INITIALISATION else")
            qualityAdapter.checkedPosition = position
            qualityAdapter.notifyDataSetChanged()
            urlRadioService = streamLink
            playAudio(urlRadioService)
        }
    }


}