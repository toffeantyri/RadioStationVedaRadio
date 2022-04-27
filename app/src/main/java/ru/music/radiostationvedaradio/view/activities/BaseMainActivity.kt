package ru.music.radiostationvedaradio.view.activities

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.*
import android.net.Uri
import android.os.Handler
import android.os.IBinder
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ExpandableListView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.yandex.mobile.ads.banner.AdSize
import com.yandex.mobile.ads.banner.BannerAdEventListener
import com.yandex.mobile.ads.common.AdRequest
import com.yandex.mobile.ads.common.AdRequestError
import com.yandex.mobile.ads.common.ImpressionData
import kotlinx.android.synthetic.main.activity_main.*
import ru.music.radiostationvedaradio.R
import ru.music.radiostationvedaradio.services.*
import ru.music.radiostationvedaradio.view.adapters.expandableList.ExpandableListAdapterForNavView
import ru.music.radiostationvedaradio.view.adapters.expandableList.ExpandedMenuModel
import ru.music.radiostationvedaradio.view.fragments.WebViewFragment
import ru.music.radiostationvedaradio.viewmodel.ViewModelMainActivity

@SuppressLint("Registered")
open class BaseMainActivity : AppCompatActivity() {


    protected lateinit var myDrawerLayout: DrawerLayout
    protected lateinit var mMenuAdapter : ExpandableListAdapterForNavView
    protected lateinit var expandableList : ExpandableListView
    protected lateinit var listDataHeader: ArrayList<ExpandedMenuModel>
    protected lateinit var listDataChild: HashMap<ExpandedMenuModel, List<String>>
    protected lateinit var navigationView: NavigationView

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

    protected var webUrl = ""
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

    protected fun setUpActionBar() {
        supportActionBar?.apply {
            setHomeButtonEnabled(true)
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp)
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

    protected fun NavigationView.setUpDrawerNavViewListener() {
        this.setNavigationItemSelectedListener {
            Log.d("MyLog", "${it.itemId}")
            when (it.itemId) {
                R.id.nav_item_exit -> {
                    alertDialogExit()
                }
                R.id.nav_item_rate_app -> {
                    val intent =
                        Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.link_on_this_app)))
                    startActivity(intent)
                }
                R.id.nav_item1 -> {
                    Log.d("MyLog", "nav click: ${it.itemId}")
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.container_frame_for_website, WebViewFragment.newInstance(webUrl)).commit()
                    drawer_menu.closeDrawer(GravityCompat.START)
                }
            }
            return@setNavigationItemSelectedListener true
        }
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

    protected var fragmentIsConnected = false
    private var doubleBackPress = false
    override fun onBackPressed() {
        if (fragmentIsConnected) {
            super.onBackPressed()
            return
        }

        if (doubleBackPress) {
            super.onBackPressed()
        }
        doubleBackPress = true
        handler.postDelayed({ doubleBackPress = false }, 2000)
        alertDialogExit()
    }

    protected fun playAudio(urlStream: String) {
        if (!serviceBound) {
            Log.d("MyLog", "StartService")
            val playerIntent = Intent(applicationContext, RadioPlayerService::class.java)
            if (this.isServiceRunning(RadioPlayerService::class.java)) {
                playerIntent.putExtra(TAG_FIRST_RUN, false)
            } else {
                playerIntent.putExtra(TAG_FIRST_RUN, true)
                playerIntent.putExtra(TAG_NEW_AUDIO_URL, urlStream)
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_play && mediaService != null) {
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
            if (drawer_menu.isDrawerOpen(GravityCompat.START)) drawer_menu.closeDrawer(GravityCompat.START)
            else drawer_menu.openDrawer(GravityCompat.START)
        }
        return super.onOptionsItemSelected(item)
    }

    protected fun prepareListData(){
        listDataHeader = arrayListOf()
        listDataChild = HashMap<ExpandedMenuModel, List<String>>()

        val item1 = ExpandedMenuModel()
        item1.apply {
            setIconName("heading1")
            item1.setIconImage(R.drawable.ic_baseline_radio_24)
        }
        listDataHeader.add(item1)

        val item2 = ExpandedMenuModel()
        item2.apply {
            setIconName("heading2")
            item2.setIconImage(R.drawable.ic_baseline_radio_24)
        }
        listDataHeader.add(item2)

        val item3 = ExpandedMenuModel()
        item3.apply {
            setIconName("heading3")
            item3.setIconImage(R.drawable.ic_baseline_radio_24)
        }
        listDataHeader.add(item3)

        val heading1 = arrayListOf<String>()
            heading1.add("SubMenu item 1")

        val heading2 = arrayListOf<String>()
        heading2.add("Submenu item 2")
        heading2.add("Submenu item 2")
        heading2.add("Submenu item 2")

        listDataChild.put(listDataHeader[0], heading1)
        listDataChild.put(listDataHeader[1], heading2)


    }

    protected fun setupDrawerContent(navigationView: NavigationView){
        navigationView.setNavigationItemSelectedListener { item ->
            item.isChecked = true
            myDrawerLayout.closeDrawers()
            true
        }
    }


}