package ru.music.radiostationvedaradio.activityes

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.*
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.yandex.mobile.ads.banner.AdSize
import com.yandex.mobile.ads.banner.BannerAdEventListener
import com.yandex.mobile.ads.common.AdRequest
import com.yandex.mobile.ads.common.AdRequestError
import com.yandex.mobile.ads.common.ImpressionData
import kotlinx.android.synthetic.main.activity_main.*
import moxy.MvpAppCompatActivity
import moxy.ktx.moxyPresenter
import ru.music.radiostationvedaradio.R
import ru.music.radiostationvedaradio.services.*
import ru.music.radiostationvedaradio.view.adapters.expandableList.ExpandableListAdapterForNavView
import ru.music.radiostationvedaradio.view.adapters.expandableList.ExpandedMenuModel
import ru.music.radiostationvedaradio.view.adapters.listview.ListViewAdapter
import ru.music.radiostationvedaradio.view.adapters.listview.ListViewItemModel
import ru.music.radiostationvedaradio.presenters.MainPresenter
import ru.music.radiostationvedaradio.view.adapters.MainView
import ru.music.radiostationvedaradio.viewmodel.ViewModelMainActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.NavController
import ru.music.radiostationvedaradio.fragments.TAG_WEB_URL

@SuppressLint("Registered")
open class BaseMainActivity : MvpAppCompatActivity(), MainView {

    protected val mainPresenter by moxyPresenter { MainPresenter() }

    private lateinit var mToolbar: Toolbar
    lateinit var navController: NavController
    lateinit var myDrawerLayout: DrawerLayout
    private lateinit var mMenuAdapter: ExpandableListAdapterForNavView
    private lateinit var expandableList: ExpandableListView
    private lateinit var listDataHeader: ArrayList<ExpandedMenuModel>
    private lateinit var listDataChild: HashMap<ExpandedMenuModel, List<String>>

    private lateinit var navigationView: NavigationView

    private lateinit var listView: ListView
    private lateinit var adapterListView: BaseAdapter
    private lateinit var listViewData: ArrayList<ListViewItemModel>


    protected val dataModel: ViewModelMainActivity by viewModels()
    protected var statusMediaPlayer = InitStatusMediaPlayer.IDLE
        set(value) {
            field = value
            dataModel.statusMediaPlayer.value = value
        }

    protected var serviceBound = false
        set(value) {
            field = value
            Log.d("MyLog", "serviceBound -> $value")
        }

    protected var mediaService: RadioPlayerService? = null

    private val handler = Handler()
    private var myMenu: Menu? = null
    private lateinit var btnPlay: MenuItem
    private lateinit var btnRefresh: MenuItem

    private var webUrl = ""
    protected var url: String = ""
        set(value) {
            field = value
            updateCheckGroupQuality(value)
            Log.d("MyLog", "Activity : url -> $value")
        }

    private fun updateCheckGroupQuality(url: String) {
        if (myMenu == null) return
        myMenu?.apply {
            findItem(R.id.action_low_quality)?.isChecked = false
            findItem(R.id.action_medium_quality)?.isChecked = false
            findItem(R.id.action_high_quality)?.isChecked = false
        }
        when (url) {
            getString(R.string.veda_radio_stream_link_low) -> {
                myMenu?.findItem(R.id.action_low_quality)?.isChecked = true
            }
            getString(R.string.veda_radio_stream_link_medium) -> {
                myMenu?.findItem(R.id.action_medium_quality)?.isChecked = true
            }
            getString(R.string.veda_radio_stream_link_high) -> {
                myMenu?.findItem(R.id.action_high_quality)?.isChecked = true
            }
        }
    }

    fun <T> Context.isServiceRunning(service: Class<T>) =
        (getSystemService(ACTIVITY_SERVICE) as ActivityManager)
            .getRunningServices(Integer.MAX_VALUE)
            .any { it.service.className == service.name }

    protected fun playAudio(urlStream: String) {
        if (!serviceBound) {
            Log.d("MyLog", "StartService")
            val playerIntent = Intent(applicationContext, RadioPlayerService::class.java)
            playerIntent.putExtra(TAG_NEW_AUDIO_URL, urlStream)
            if (this.isServiceRunning(RadioPlayerService::class.java)) {
                playerIntent.putExtra(TAG_FIRST_RUN, false)
            } else {
                playerIntent.putExtra(TAG_FIRST_RUN, true)
            }
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
            url = mediaService?.getPlayingURL() ?: ""
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

    protected fun registerBroadcastStateService() {
        registerReceiver(broadcastStateServiceListener, IntentFilter(Broadcast_STATE_SERVICE))
    }

    protected fun loadAndShowBanner() {
        main_banner.apply {
            setAdUnitId(getString(R.string.yandex_banner_desc_id_test))
            setAdSize(AdSize.BANNER_320x50)
        }

        val adRequest = AdRequest.Builder().build()

        main_banner.setBannerAdEventListener(object : BannerAdEventListener {
            override fun onAdLoaded() {
                Log.d("MyLog", "Banner Loaded Ok")
            }

            override fun onAdFailedToLoad(p0: AdRequestError) {
                Log.d("MyLog", "Banner Load Fail")
            }

            override fun onAdClicked() {
                Log.d("MyLog", "Ad Clicked")
            }

            override fun onLeftApplication() {
            }

            override fun onReturnedToApplication() {
            }

            override fun onImpression(p0: ImpressionData?) {
            }
        })
        main_banner.loadAd(adRequest)
    }

    private var doubleBackPress = false
    override fun onBackPressed() {
        if (!doubleBackPress) {
            super.onBackPressed()
        } else {
            alertDialogExit()
        }
        doubleBackPress = true
        handler.postDelayed({ doubleBackPress = false }, 2000)

    }

    private fun alertDialogExit() {
        val aDialog = AlertDialog.Builder(this)
        aDialog.apply {
            setMessage(R.string.alert_mes_exit)
                .setCancelable(true)
                .setPositiveButton(
                    R.string.alert_mes_yes_all
                ) { _, _ ->
                    mediaService?.stopForeground(true)
                    mediaService?.stopSelf()
                    super.onBackPressed()
                }
        }
        aDialog.setNegativeButton(
            R.string.alert_mes_yes
        ) { _, _ -> super.onBackPressed() }
        aDialog.setNeutralButton(
            R.string.alert_mes_no
        ) { dialog, _ -> dialog.cancel() }
        val alert = aDialog.create()
        alert.show()

    }


    //---------------------initNavigationView start--------------------------------
    protected fun initExpandableListInNavView() {
        myDrawerLayout = drawer_menu
        expandableList = exp_list_nav_menu
        navigationView = draw_navView
        setupDrawerContent(navigationView)
        prepareExpListData()
        mMenuAdapter =
            ExpandableListAdapterForNavView(this, listDataHeader, listDataChild, expandableList)
        expandableList.setAdapter(mMenuAdapter)

        expandableList.setOnChildClickListener { parent, v, groupPosition, childPosition, id ->
            //Log.d("MyLog", "$childPosition child in $groupPosition parent ")
            if (groupPosition == 0) {
                when (childPosition) {
                    0 -> {
                        //Log.d("MyLog", "nav click: $childPosition")
                        val newWebUrl = getString(R.string.veda_radio_site)
                        if (webUrl != newWebUrl) {
                            webUrl = newWebUrl
                            replaceWebFragmentWithUrl(webUrl)
                        }
                        drawer_menu.closeDrawer(GravityCompat.START)
                    }
                    1 -> {
                        //Log.d("MyLog", "nav click: $childPosition")
                        val newWebUrl = getString(R.string.torsunov_site)
                        if (webUrl != newWebUrl) {
                            webUrl = newWebUrl
                            replaceWebFragmentWithUrl(webUrl)
                        }
                        drawer_menu.closeDrawer(GravityCompat.START)
                    }

                    2 -> {
                        //Log.d("MyLog", "nav click: $childPosition")
                        val newWebUrl = getString(R.string.provedy_site)
                        if (webUrl != newWebUrl) {
                            webUrl = newWebUrl
                            replaceWebFragmentWithUrl(webUrl)
                        }
                        drawer_menu.closeDrawer(GravityCompat.START)
                    }
                }
            }
            true
        }
    }

    private fun replaceWebFragmentWithUrl(webUrl: String) {
        val bundle = Bundle()
        bundle.putString(TAG_WEB_URL, webUrl)
        navController.navigate(R.id.action_mainFragment_to_webViewFragment, bundle)
    }

    private fun prepareExpListData() {
        listDataHeader = arrayListOf()
        listDataChild = HashMap<ExpandedMenuModel, List<String>>()

        //header point
        val headerItem0 = ExpandedMenuModel().apply {
            setIconName(getString(R.string.link_header_name))
            setIconImage(R.drawable.ic_collections_bookmark_black_24dp)
        }
        listDataHeader.add(headerItem0)

        //subitem point
        val subItem = arrayListOf<String>()
        subItem.apply {
            add(getString(R.string.veda_radio_site))
            add(getString(R.string.torsunov_site))
            add(getString(R.string.provedy_site))
        }

        listDataChild.put(listDataHeader[0], subItem)
    }

    protected fun initListViewInNavView() {
        prepareListViewData()
        adapterListView = ListViewAdapter(listViewData)
        listView = listview_nav_menu
        listView.adapter = adapterListView
        listView.setOnItemClickListener { _, _, position, _ ->
            when (position) {
                0 -> {
                    val intent =
                        Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.link_on_this_app)))
                    startActivity(intent)
                }
                1 -> alertDialogExit()
            }
        }

    }

    private fun prepareListViewData() {
        listViewData = arrayListOf<ListViewItemModel>()
        val rate = ListViewItemModel().apply {
            setTitle(getString(R.string.item_about_app))
            setIconId(R.drawable.ic_baseline_star_rate_24)
        }
        val exit = ListViewItemModel().apply {
            setTitle(getString(R.string.item_exit))
            setIconId(R.drawable.ic_exit)
        }
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


    //---------------------initActionBar--------------------------------
    protected fun setUpActionBar() {
        mToolbar = main_toolbar
        setSupportActionBar(mToolbar)
        supportActionBar?.apply {
            setHomeButtonEnabled(true)
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp)
        }
        title = getString(R.string.app_name)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        if (menu == null) {
            return super.onCreateOptionsMenu(menu)
        }
        myMenu = menu
        updateCheckGroupQuality(url)
        btnPlay = menu.findItem(R.id.action_play)
        btnRefresh = menu.findItem(R.id.action_refresh)

        dataModel.statusMediaPlayer.observe(this) {
            if (it == InitStatusMediaPlayer.PLAYING) btnPlay.setIcon(R.drawable.ic_baseline_pause_circle_filled_24)
            else if (it != InitStatusMediaPlayer.PLAYING) btnPlay.setIcon(R.drawable.ic_baseline_play_circle_filled_24)
        }
        dataModel.statusMediaPlayer.observe(this) {
            if (it == InitStatusMediaPlayer.INITIALISATION) {
                btnRefresh.setActionView(R.layout.action_progressbar)
                btnRefresh.expandActionView()
            } else btnRefresh.actionView = null

        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_play) {
            Log.d("MyLog", "action_play click. isPlaying: ${dataModel.statusMediaPlayer.value}")
            when (dataModel.statusMediaPlayer.value) {
                InitStatusMediaPlayer.INIT_COMPLETE -> mediaService?.playMedia()
                InitStatusMediaPlayer.PLAYING -> mediaService?.pauseMedia()
                InitStatusMediaPlayer.IDLE -> playAudio(url)
                InitStatusMediaPlayer.INITIALISATION -> {
                    Toast.makeText(this, "Loading", Toast.LENGTH_SHORT).show()
                }
            }
        } else if (item.itemId == R.id.action_refresh) {
            playAudio(url)
        } else if (item.itemId == R.id.action_low_quality) {
            url = getString(R.string.veda_radio_stream_link_low)
            playAudio(url)
        } else if (item.itemId == R.id.action_medium_quality) {
            url = getString(R.string.veda_radio_stream_link_medium)
            playAudio(url)
        } else if (item.itemId == R.id.action_high_quality) {
            url = getString(R.string.veda_radio_stream_link_high)
            playAudio(url)
        } else if (item.itemId == android.R.id.home) {
            if (myDrawerLayout.isDrawerOpen(GravityCompat.START)) myDrawerLayout.closeDrawer(
                GravityCompat.START
            )
            else myDrawerLayout.openDrawer(GravityCompat.START)
        }
        return super.onOptionsItemSelected(item)
    }


    //viewMVP methods
    override fun displayTcitataDnya(data: String) {
        dataModel.stringTcitataDnya.value = data
        //todo добавить наблюдателя в фрагменте когда будем получать String через repo
    }

    override fun displayError(error: Throwable) {

    }

    override fun setLoading(flag: Boolean) {

    }
    //---------------------initActionBar--------------------------------


}