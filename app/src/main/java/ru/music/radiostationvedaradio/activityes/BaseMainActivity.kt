package ru.music.radiostationvedaradio.activityes

import android.annotation.SuppressLint
import android.content.*
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.yandex.mobile.ads.banner.BannerAdEventListener
import com.yandex.mobile.ads.banner.BannerAdSize
import com.yandex.mobile.ads.banner.BannerAdView
import com.yandex.mobile.ads.common.AdRequest
import com.yandex.mobile.ads.common.AdRequestError
import com.yandex.mobile.ads.common.ImpressionData
import kotlinx.coroutines.*
import ru.music.radiostationvedaradio.R
import ru.music.radiostationvedaradio.data.model.MetadataRadioService
import ru.music.radiostationvedaradio.data.model.menus.ExpandableChildItem
import ru.music.radiostationvedaradio.data.model.menus.ExpandableMenuItem
import ru.music.radiostationvedaradio.data.model.menus.SimpleMenuItem
import ru.music.radiostationvedaradio.databinding.ActivityMainBinding
import ru.music.radiostationvedaradio.screens.TAG_WEB_URL
import ru.music.radiostationvedaradio.services.*
import ru.music.radiostationvedaradio.utils.AUTHOR
import ru.music.radiostationvedaradio.utils.SONG_NAME
import ru.music.radiostationvedaradio.utils.exitDialog
import ru.music.radiostationvedaradio.utils.myLog
import ru.music.radiostationvedaradio.utils.openIntentUrl
import ru.music.radiostationvedaradio.view.adapters.expandableList.ExpandableListAdapterForNavView
import ru.music.radiostationvedaradio.view.adapters.listview.ListViewAdapter
import ru.music.radiostationvedaradio.viewmodel.ViewModelMainActivity


@SuppressLint("Registered")
open class BaseMainActivity : AppCompatActivity() {

    //------------------------------------------------------------------

    internal lateinit var binding: ActivityMainBinding

    protected val viewModel: ViewModelMainActivity by viewModels()

    var webUrl: String? = "" // url для WebFragment public для webFragment


    private val mMenuAdapter: ExpandableListAdapterForNavView by lazy {
        ExpandableListAdapterForNavView(this, listDataHeader, listDataChild)
    }

    private val listDataHeader: List<ExpandableMenuItem> by lazy {
        listOf(
            ExpandableMenuItem(
                getString(R.string.link_header_name),
                R.drawable.ic_bookmark
            )
        )
    }
    private val listDataChild: HashMap<ExpandableMenuItem, List<ExpandableChildItem>> by lazy {
        hashMapOf(
            Pair(
                listDataHeader[0],
                listOf(
                    ExpandableChildItem(R.string.name_veda_radio_site, R.string.veda_radio_site),
                    ExpandableChildItem(R.string.name_torsunov_site, R.string.torsunov_site),
                    ExpandableChildItem(R.string.name_provedy_site, R.string.provedy_site)
                )
            )
        )
    }

    private val listViewData: List<SimpleMenuItem> by lazy {
        listOf(
            SimpleMenuItem(getString(R.string.bad_advice_header_name), R.drawable.ic_note),
            SimpleMenuItem(getString(R.string.item_about_app), R.drawable.ic_star_rate),
            SimpleMenuItem(getString(R.string.item_exit), R.drawable.ic_exit)
        )
    }
    private val adapterListView: BaseAdapter by lazy {
        ListViewAdapter(listViewData)
    }


    //----------------------------s service----------------------------------------------------------------------
    protected var statusMediaPlayer = InitStatusMediaPlayer.IDLE
        set(value) {
            field = value
            viewModel.statusMediaPlayer.value = value
        }

    protected var metadataRadioService: MetadataRadioService? = null
        set(value) {
            field = value
            viewModel.metadataOfPlayer.value = value
        }

    protected var serviceBound = false
    protected var mediaService: RadioPlayerService? = null


    protected fun playAudio(urlStream: String) {
        if (!serviceBound) {
            Log.d("MyLog", "StartService $urlStream")
            val playerIntent = Intent(applicationContext, RadioPlayerService::class.java)
            playerIntent.putExtra(TAG_NEW_AUDIO_URL, urlStream)
            applicationContext.startForegroundService(playerIntent)
            bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE)
        } else {
            Log.d("MyLog", "service is already bound")
            val broadcastIntent = Intent(Broadcast_NEW_AUDIO)
            broadcastIntent.putExtra(TAG_NEW_AUDIO_URL, urlStream)
            broadcastIntent.putExtra(TAG_FIRST_RUN, false)
            sendBroadcast(broadcastIntent)
        }
    }


    protected val serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as RadioPlayerService.LocalBinder
            mediaService = binder.getService()
            serviceBound = true
            statusMediaPlayer = mediaService?.getStatusMediaMplayer() ?: InitStatusMediaPlayer.IDLE
            metadataRadioService = mediaService?.getMetadata()
            viewModel.playingUrl.value = mediaService?.getPlayingURL() ?: ""
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            serviceBound = false
            mediaService = null
        }
    }

    private val broadcastStateServiceListener = object : BroadcastReceiverForPlayerService() {
        override fun onReceive(context: Context?, intent: Intent?) {
            statusMediaPlayer =
                intent?.getSerializableExtra(TAG_STATE_SERVICE) as InitStatusMediaPlayer
        }
    }

    private val broadcastServiceSongReceiver = object : BroadcastReceiverForPlayerService() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val author = intent?.getStringExtra(AUTHOR) ?: getString(R.string.app_name)
            val song = intent?.getStringExtra(SONG_NAME) ?: getString(R.string.app_name)
            myLog("METADATA : $author and $song")
            viewModel.metadataOfPlayer.value = MetadataRadioService(author, song)
        }
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    protected fun registerBroadcastStateService() {
        registerReceiver(broadcastStateServiceListener, IntentFilter(Broadcast_STATE_SERVICE))
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    protected fun registerBroadcastNewSongService() {
        registerReceiver(broadcastServiceSongReceiver, IntentFilter(Broadcast_METADATA_SERVICE))
    }

    //----------------------------e service-------------------------------------------------------------------

    // -----------------------------------------other init ------------------------------------------
    protected fun loadAndShowBanner() {
        val banner = findViewById<BannerAdView>(R.id.main_banner).apply {
            setAdUnitId(getString(R.string.yandex_banner_desc_id_test))
            setAdSize(BannerAdSize.inlineSize(context, 320, 50))
        }
        val adRequest = AdRequest.Builder().build()
        banner.setBannerAdEventListener(object : BannerAdEventListener {
            override fun onAdLoaded() {
                Log.d("MyLog", "Banner Loaded Ok")
            }

            override fun onAdFailedToLoad(p0: AdRequestError) {
                Log.d("MyLog", "Banner Load Fail code : ${p0.code} description: ${p0.description}")
            }

            override fun onAdClicked() {}

            override fun onLeftApplication() {}

            override fun onReturnedToApplication() {}

            override fun onImpression(p0: ImpressionData?) {}
        })
        banner.loadAd(adRequest)
    }

    private var doubleBackPress = false

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.main_nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        if (navController.currentDestination?.id != navController.graph.startDestinationId) {
            super.onBackPressed()
            return
        }
        if (doubleBackPress) super.onBackPressed()
        doubleBackPress = true
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({ doubleBackPress = false }, 2000)
        this.exitDialog(onFullExit = {})
    }

    // ----------------------------------------- other init ------------------------------------------

    //---------------------initNavigationView start--------------------------------

    private fun navigateWebFragmentWithUrl(webUrl: String) {
        val bundle = Bundle()
        bundle.putString(TAG_WEB_URL, webUrl)
        findNavController(R.id.main_nav_host_fragment).navigate(R.id.webViewFragment, bundle)
    }

    protected fun initExpandableListInNavView() {
        with(binding) {
            drawNavView.setNavigationItemSelectedListener { item ->
                item.isChecked = true
                binding.drawerMenu.closeDrawers()
                true
            }

            expListNavMenu.setAdapter(mMenuAdapter)
            expListNavMenu.setOnChildClickListener { _, _, groupPosition, childPosition, _ ->
                val key = listDataHeader[groupPosition]
                val list = listDataChild[key]
                list?.get(childPosition)?.let {
                    val newUrl = getString(it.linkUrl)
                    webUrl = newUrl
                    navigateWebFragmentWithUrl(newUrl)
                    drawerMenu.closeDrawer(GravityCompat.START)
                }
                true
            }
        }
    }


    protected fun initListViewOfNavMenuListener() {
        binding.listviewNavMenu.adapter = adapterListView
        binding.listviewNavMenu.setOnItemClickListener { _, _, position, _ ->
            when (position) {
                0 -> {
                    binding.drawerMenu.closeDrawer(GravityCompat.START)
                    Handler(Looper.getMainLooper()).postDelayed({
                        findNavController(R.id.main_nav_host_fragment).navigate(R.id.badAdviceFragment)
                    }, 300)
                }

                1 -> this.openIntentUrl(getString(R.string.link_on_this_app))
                2 -> this.exitDialog(onFullExit = {})
            }
        }
    }

    protected fun initToolbar() {
        with(binding) {
            toolbarContainer.actionHome.setOnClickListener {
                CoroutineScope(Dispatchers.Main).launch {
                    if (binding.drawerMenu.isDrawerOpen(GravityCompat.START)) binding.drawerMenu.closeDrawer(
                        GravityCompat.START
                    )
                    else binding.drawerMenu.openDrawer(GravityCompat.START)
                }
            }
        }
    }


}