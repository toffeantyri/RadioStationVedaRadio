package ru.music.radiostationvedaradio.activityes

import android.annotation.SuppressLint
import android.app.Service
import android.content.*
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.media3.common.MediaItem
import androidx.media3.session.LibraryResult
import androidx.media3.session.MediaBrowser
import androidx.media3.session.SessionToken
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.navigation.NavigationView
import com.google.common.util.concurrent.ListenableFuture
import com.yandex.mobile.ads.banner.BannerAdEventListener
import com.yandex.mobile.ads.banner.BannerAdSize
import com.yandex.mobile.ads.banner.BannerAdView
import com.yandex.mobile.ads.common.AdRequest
import com.yandex.mobile.ads.common.AdRequestError
import com.yandex.mobile.ads.common.ImpressionData
import kotlinx.coroutines.*
import ru.music.radiostationvedaradio.R
import ru.music.radiostationvedaradio.busines.model.MetadataRadioService
import ru.music.radiostationvedaradio.databinding.ActivityMainBinding
import ru.music.radiostationvedaradio.screens.TAG_WEB_URL
import ru.music.radiostationvedaradio.services.*
import ru.music.radiostationvedaradio.services.player_service.PlayerService
import ru.music.radiostationvedaradio.utils.AUTHOR
import ru.music.radiostationvedaradio.utils.SONG_NAME
import ru.music.radiostationvedaradio.utils.invisible
import ru.music.radiostationvedaradio.utils.myLog
import ru.music.radiostationvedaradio.utils.show
import ru.music.radiostationvedaradio.view.adapters.expandableList.ExpandableListAdapterForNavView
import ru.music.radiostationvedaradio.view.adapters.expandableList.ExpandedMenuModel
import ru.music.radiostationvedaradio.view.adapters.listview.ListViewAdapter
import ru.music.radiostationvedaradio.view.adapters.listview.ListViewItemModel
import ru.music.radiostationvedaradio.viewmodel.ViewModelMainActivity


@SuppressLint("Registered")
open class BaseMainActivity : AppCompatActivity() {

    private lateinit var browserFuture: ListenableFuture<MediaBrowser>
    private val browser: MediaBrowser?
        get() = if (browserFuture.isDone && !browserFuture.isCancelled) browserFuture.get() else null

    private val treePathStack: ArrayDeque<MediaItem> = ArrayDeque()
    var subItemMediaList: MutableList<MediaItem> = mutableListOf()
    protected lateinit var mediaListAdapter: FolderMediaItemArrayAdapter

    //------------------------------------------------------------------

    internal lateinit var binding: ActivityMainBinding

    protected val viewModel: ViewModelMainActivity by viewModels()

    var webUrl: String? = "" // url для WebFragment public для webFragment


    //---------------------- s drawer menu---------------------
    private lateinit var myDrawerLayout: DrawerLayout
    private lateinit var mMenuAdapter: ExpandableListAdapterForNavView
    private lateinit var expandableList: ExpandableListView
    private lateinit var listDataHeader: ArrayList<ExpandedMenuModel>
    private lateinit var listDataChild: HashMap<ExpandedMenuModel, List<String>>
    private lateinit var parentNavView: NavigationView
    private lateinit var listView: ListView
    private lateinit var adapterListView: BaseAdapter
    private lateinit var listViewData: ArrayList<ListViewItemModel>
    //----------------------e drawer menu---------------------

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
    protected var urlRadioService: String = ""
        set(value) {
            field = value
            updateCheckGroupQuality(value)
        }

    private fun updateCheckGroupQuality(url: String) {
        //todo need add my adapter from sara
//        myMenu?.apply {
//            findItem(R.id.action_low_quality)?.isChecked = false
//            findItem(R.id.action_medium_quality)?.isChecked = false
//            findItem(R.id.action_high_quality)?.isChecked = false
//        }
//
//        when (url) {
//            getString(R.string.veda_radio_stream_link_low) -> {
//                myMenu?.findItem(R.id.action_low_quality)?.isChecked = true
//            }
//
//            getString(R.string.veda_radio_stream_link_medium) -> {
//                myMenu?.findItem(R.id.action_medium_quality)?.isChecked = true
//            }
//
//            getString(R.string.veda_radio_stream_link_high) -> {
//                myMenu?.findItem(R.id.action_high_quality)?.isChecked = true
//            }
//        }
    }

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

    @androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
    protected fun startPlayerService(urlStream: String) {

        browserFuture =
            MediaBrowser.Builder(
                this,
                SessionToken(this, ComponentName(this, PlayerService::class.java))
            )
                .buildAsync()
        browserFuture.addListener({ pushRoot() }, ContextCompat.getMainExecutor(this))
    }


    private fun pushRoot() {
        // browser can be initialized many times
        // only push root at the first initialization
        if (!treePathStack.isEmpty()) {
            return
        }
        val browser = this.browser ?: return
        val rootFuture = browser.getLibraryRoot(/* params= */ null)
        rootFuture.addListener(
            {
                val result: LibraryResult<MediaItem> = rootFuture.get()!!
                val root: MediaItem = result.value!!
                pushPathStack(root)
            },
            ContextCompat.getMainExecutor(this)
        )
    }

    private fun pushPathStack(mediaItem: MediaItem) {
        treePathStack.addLast(mediaItem)
        displayChildrenList(treePathStack.last())
    }


    private fun displayChildrenList(mediaItem: MediaItem) {
        val browser = this.browser ?: return

        supportActionBar?.setDisplayHomeAsUpEnabled(treePathStack.size != 1)
        val childrenFuture =
            browser.getChildren(
                mediaItem.mediaId,
                /* page= */ 0,
                /* pageSize= */ Int.MAX_VALUE,
                /* params= */ null
            )

        subItemMediaList.clear()
        childrenFuture.addListener(
            {
                val result = childrenFuture.get()!!
                val children = result.value!!
                subItemMediaList.addAll(children)
                mediaListAdapter.notifyDataSetChanged()
            },
            ContextCompat.getMainExecutor(this)
        )
    }


    protected val serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as RadioPlayerService.LocalBinder
            mediaService = binder.getService()
            serviceBound = true
            statusMediaPlayer = mediaService?.getStatusMediaMplayer() ?: InitStatusMediaPlayer.IDLE
            metadataRadioService = mediaService?.getMetadata()
            urlRadioService = mediaService?.getPlayingURL() ?: ""
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
        alertDialogExit()
    }

    private fun alertDialogExit() {
        val aDialog = AlertDialog.Builder(this)
        aDialog.apply {
            setMessage(R.string.alert_mes_exit)
                .setCancelable(true)
                .setPositiveButton(
                    R.string.alert_mes_yes_all
                ) { _, _ ->
                    mediaService?.stopForeground(Service.STOP_FOREGROUND_DETACH)
                    mediaService?.stopSelf()
                    finish()
                }
        }
        aDialog.setNegativeButton(
            R.string.alert_mes_yes
        ) { _, _ ->
            finish()
        }
        aDialog.setNeutralButton(
            R.string.alert_mes_no
        ) { dialog, _ -> dialog.cancel() }
        val alert = aDialog.create()
        alert.show()

    }
    // ----------------------------------------- other init ------------------------------------------

    //---------------------initNavigationView start--------------------------------

    private fun navigateWebFragmentWithUrl(webUrl: String) {
        val bundle = Bundle()
        bundle.putString(TAG_WEB_URL, webUrl)
        findNavController(R.id.main_nav_host_fragment).navigate(R.id.webViewFragment, bundle)
    }

    private fun navigateMainFragmentToBadAdvancedFrag() {
        findNavController(R.id.main_nav_host_fragment).navigate(R.id.badAdviceFragment)
    }

    protected fun initExpandableListInNavView() {
        myDrawerLayout = findViewById(R.id.drawer_menu)
        expandableList = findViewById(R.id.exp_list_nav_menu)
        parentNavView = findViewById(R.id.draw_navView)
        setupDrawerContent(parentNavView)
        prepareExpListData()
        mMenuAdapter =
            ExpandableListAdapterForNavView(this, listDataHeader, listDataChild, expandableList)
        expandableList.setAdapter(mMenuAdapter)
        fun navigateWebFragWithUrlCloseDraver(url: String) {
            webUrl = url
            navigateWebFragmentWithUrl(url)
            myDrawerLayout.closeDrawer(GravityCompat.START)
        }
        expandableList.setOnChildClickListener { _, _, groupPosition, childPosition, _ ->
            if (groupPosition == 0) {
                when (childPosition) {
                    0 -> navigateWebFragWithUrlCloseDraver(getString(R.string.veda_radio_site))
                    1 -> navigateWebFragWithUrlCloseDraver(getString(R.string.torsunov_site))
                    2 -> navigateWebFragWithUrlCloseDraver(getString(R.string.provedy_site))
                }
            }
            true
        }
    }

    private fun prepareExpListData() {
        listDataHeader = arrayListOf()
        listDataChild = HashMap<ExpandedMenuModel, List<String>>()

        //headers
        val headerItem0 = ExpandedMenuModel().apply {
            setIconName(getString(R.string.link_header_name))
            setIconImage(R.drawable.ic_bookmark)
        }
        listDataHeader.add(headerItem0)

        //subitems
        val subItem0 = arrayListOf<String>()
        subItem0.apply {
            add(getString(R.string.name_veda_radio_site))
            add(getString(R.string.name_torsunov_site))
            add(getString(R.string.name_provedy_site))
        }

        listDataChild[listDataHeader[0]] = subItem0
    }


    protected fun initListViewOfNavMenuListener() {
        prepareListViewData()
        adapterListView = ListViewAdapter(listViewData)
        listView = findViewById(R.id.listview_nav_menu)
        listView.adapter = adapterListView


        listView.setOnItemClickListener { _, _, position, _ ->
            when (position) {
                0 -> {
                    CoroutineScope(Dispatchers.Main).launch {
                        findViewById<DrawerLayout>(R.id.drawer_menu).closeDrawer(GravityCompat.START)
                        delay(300)
                        navigateMainFragmentToBadAdvancedFrag()
                    }

                }

                1 -> {
                    val intent =
                        Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.link_on_this_app)))
                    startActivity(intent)
                }

                2 -> alertDialogExit()
            }
        }

    }

    private fun prepareListViewData() {
        listViewData = arrayListOf<ListViewItemModel>()

        val badAdvice = ListViewItemModel().apply {
            setTitle(getString(R.string.bad_advice_header_name))
            setIconId(R.drawable.ic_note)
        }

        val rate = ListViewItemModel().apply {
            setTitle(getString(R.string.item_about_app))
            setIconId(R.drawable.ic_star_rate)
        }
        val exit = ListViewItemModel().apply {
            setTitle(getString(R.string.item_exit))
            setIconId(R.drawable.ic_exit)
        }
        listViewData.add(badAdvice)
        listViewData.add(rate)
        listViewData.add(exit)
    }

    private fun setupDrawerContent(navigationView: NavigationView) {
        navigationView.setNavigationItemSelectedListener { item ->
            item.isChecked = true
            myDrawerLayout.closeDrawers()
            true
        }
    }

    //---------------------initNavigationView end--------------------------------


    //---------------------initToolbar--------------------------------
    protected fun initToolbar() {
        with(binding) {
            viewModel.statusMediaPlayer.observe(this@BaseMainActivity) {
                setViewByStatusMediaPlayer(it)
            }

            toolbarContainer.actionPlay.setOnClickListener {
                //todo
                viewModel.statusMediaPlayer.value?.let {
                    buttonPlayAction(it)
                }
            }

            toolbarContainer.actionRefresh.setOnClickListener {
                playAudio(urlRadioService)
            }

            toolbarContainer.actionHome.setOnClickListener {
                CoroutineScope(Dispatchers.Main).launch {
                    if (myDrawerLayout.isDrawerOpen(GravityCompat.START)) myDrawerLayout.closeDrawer(
                        GravityCompat.START
                    )
                    else myDrawerLayout.openDrawer(GravityCompat.START)
                }
            }

            toolbarContainer.actionQualityContainer.setOnClickListener {
                with(binding) {


                }
            }
        }
    }


    private fun setViewByStatusMediaPlayer(status: InitStatusMediaPlayer) {
        with(binding) {
            if (status == InitStatusMediaPlayer.INITIALISATION) {
                toolbarContainer.actionRefresh.invisible()
                toolbarContainer.refreshProgressbar.show()
            } else {
                toolbarContainer.actionRefresh.show()
                toolbarContainer.refreshProgressbar.invisible()
            }

            if (status == InitStatusMediaPlayer.PLAYING) {
                slidingPanelPlayer.mainEqualizer.animateBars()
                toolbarContainer.actionPlay.setImageResource(R.drawable.ic_pause)
            } else {
                toolbarContainer.actionPlay.setImageResource(R.drawable.ic_play_filled)
                slidingPanelPlayer.mainEqualizer.stopBars()
            }
        }
    }

    private fun buttonPlayAction(statusService: InitStatusMediaPlayer) {
        when (statusService) {
            InitStatusMediaPlayer.INIT_COMPLETE -> mediaService?.playMedia()
            InitStatusMediaPlayer.PLAYING -> mediaService?.pauseMedia()
            InitStatusMediaPlayer.IDLE -> playAudio(urlRadioService)
            InitStatusMediaPlayer.INITIALISATION -> {
                Toast.makeText(this, "Loading", Toast.LENGTH_SHORT).show()
            }
        }
    }

    //-------------------init Bottom App Bar (PlayerPanel)------------------
    protected fun initPlayerPanel() {
        with(binding.slidingPanelPlayer) {
            fabPlayPause.setOnClickListener {
                viewModel.statusMediaPlayer.value?.let { buttonPlayAction(it) }
            }
            viewModel.statusMediaPlayer.observe(this@BaseMainActivity) {
                if (it == InitStatusMediaPlayer.PLAYING) {
                    mainEqualizer.animateBars()
                    fabPlayPause.setImageResource(android.R.drawable.ic_media_pause)
                } else {
                    fabPlayPause.setImageResource(android.R.drawable.ic_media_play)
                    mainEqualizer.stopBars()
                }
            }
            viewModel.metadataOfPlayer.observe(this@BaseMainActivity) {
                tvSongAutor.text = it.artist
                tvSongTrack.text = it.song
            }
        }
    }

    //-------------------init Bottom App Bar (PlayerPanel)------------------

    class FolderMediaItemArrayAdapter(
        context: Context,
        viewID: Int,
        mediaItemList: List<MediaItem>
    ) : ArrayAdapter<MediaItem>(context, viewID, mediaItemList) {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val mediaItem = getItem(position)!!
            val returnConvertView =
                convertView ?: LayoutInflater.from(context)
                    .inflate(R.layout.folder_items, parent, false)

            returnConvertView.findViewById<TextView>(R.id.media_item).text =
                mediaItem.mediaMetadata.title
            return returnConvertView
        }
    }
}