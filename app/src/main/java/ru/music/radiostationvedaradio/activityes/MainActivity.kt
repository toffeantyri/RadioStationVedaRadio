package ru.music.radiostationvedaradio.activityes

import android.Manifest
import android.content.ComponentName
import android.media.AudioManager
import android.net.Uri
import android.os.Bundle
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.media3.common.MediaItem
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import ru.music.radiostationvedaradio.R
import ru.music.radiostationvedaradio.databinding.ActivityMainBinding
import ru.music.radiostationvedaradio.services.InitStatusMediaPlayer
import ru.music.radiostationvedaradio.services.player_service.RadioMediaService
import ru.music.radiostationvedaradio.utils.checkPermissionSingle
import ru.music.radiostationvedaradio.utils.showToast
import ru.music.radiostationvedaradio.view.adapters.OnFilterClickListener
import ru.music.radiostationvedaradio.view.adapters.filter_adapter.MenuArrayAdapter


class MainActivity : BaseMainActivity(), OnFilterClickListener {

    private var controllerFuture: ListenableFuture<MediaController>? = null
    private lateinit var controller: MediaController

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

        initToolbar()
        initQualityChooser()
        initExpandableListInNavView()
        initListViewOfNavMenuListener()
        registerBroadcastStateService()
        registerBroadcastNewSongService()
        loadAndShowBanner()

        checkPermissionSingle(Manifest.permission.READ_PHONE_STATE) {
            //playAudio(urlRadioService)

        }
    }

    override fun onStart() {
        super.onStart()
        val sessionToken = SessionToken(this, ComponentName(this, RadioMediaService::class.java))
        controllerFuture = MediaController.Builder(this, sessionToken).buildAsync()
        controllerFuture?.addListener({
            controllerFuture?.let {
                controller = it.get()
                binding.slidingPanelPlayer.playerView.player = controller
                controller.pushUrl(viewModel.getPlayingUrl())
            }
        }, MoreExecutors.directExecutor())
    }

    override fun onStop() {
        controllerFuture?.let { controller ->
            MediaController.releaseFuture(controller)
        }
        super.onStop()
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
        when (position) {
            0 -> setQualityAndPlay(getString(R.string.veda_radio_stream_link_low), position)
            1 -> setQualityAndPlay(getString(R.string.veda_radio_stream_link_medium), position)
            2 -> setQualityAndPlay(getString(R.string.veda_radio_stream_link_high), position)
        }
    }

    private fun setQualityAndPlay(streamLink: String, position: Int) {
        if (viewModel.statusMediaPlayer.value == InitStatusMediaPlayer.INITIALISATION) {
            this.showToast(getString(R.string.error_loading))
        } else {
            qualityAdapter.checkedPosition = position
            qualityAdapter.notifyDataSetChanged()
            viewModel.playingUrl.value = streamLink
            playAudio(streamLink)
        }
    }

    private fun MediaController.pushUrl(urlStream: String) {
        this.apply {
            val uri = Uri.parse(urlStream)
            val newItem = MediaItem.Builder().setMediaId(urlStream).setUri(uri).build()
            setMediaItem(newItem)
            prepare()
            playWhenReady = true
        }
    }


}