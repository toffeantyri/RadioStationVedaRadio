package ru.music.radiostationvedaradio.view

import android.content.*
import android.media.AudioManager
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import kotlinx.android.synthetic.main.activity_main.*
import ru.music.radiostationvedaradio.R
import ru.music.radiostationvedaradio.services.*
import ru.music.radiostationvedaradio.viewmodel.ViewModelMainActivity

class MainActivity : AppCompatActivity() {

    val dataModel: ViewModelMainActivity by viewModels()
    var STATE_OF_SERVICE_A = InitStatusMediaPlayer.IDLE
        set(value) {
            field = value
            dataModel.statusMediaPlayer.value = value
        }

    var serviceBound = false
        set(value) {
            field = value
            Log.d("MyLog", "serviceBound -> $value")
        }
    var mediaService: RadioPlayerService? = null

    var url: String = ""
        set(value) {
            field = value
            updateCheckGroupQuality(value)
        }

    var myMenu: Menu? = null
    lateinit var btnPlay: MenuItem
    lateinit var btnRefresh: MenuItem

    private val serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as RadioPlayerService.LocalBinder
            mediaService = binder.getService()
            serviceBound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            serviceBound = false
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setUpActionBar()
        url = getString(R.string.veda_radio_stream_link_low)
        registerBroadcastStateService()
        playAudio(url)

    }

    override fun onPause() {
        super.onPause()
        Log.d("MyLog", "MainActivity onPause")
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
        volumeControlStream = AudioManager.STREAM_MUSIC
        Log.d("MyLog", "MainActivity onResume")

    }

    override fun onDestroy() {
        if (serviceBound) {
            unbindService(serviceConnection)
            mediaService?.stopSelf()
        }
        super.onDestroy()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        myMenu = menu!!
        updateCheckGroupQuality(url)
        btnPlay = menu?.findItem(R.id.action_play)!!
        btnRefresh = menu?.findItem(R.id.action_refresh)!!

        dataModel.statusMediaPlayer.observe(this) {
            Log.d("MyLog", "observe: $it")
            if (it == InitStatusMediaPlayer.PLAYING) btnPlay.setIcon(R.drawable.ic_baseline_pause_circle_filled_24)
            if (it != InitStatusMediaPlayer.PLAYING) btnPlay.setIcon(R.drawable.ic_baseline_play_circle_filled_24)
        }

        dataModel.statusMediaPlayer.observe(this) {
            Log.d("MyLog", "observe: $it")
            if (it == InitStatusMediaPlayer.INITIALISATION) {
                btnRefresh.setActionView(R.layout.action_progressbar)
                btnRefresh.expandActionView()
            } else {
                btnRefresh.actionView = null
            }
        }
        return super.onCreateOptionsMenu(menu)
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
        }
        if (item.itemId == R.id.action_refresh) {
            playAudio(url)
        }
        when (item.itemId) {
            R.id.action_low_quality -> {
                url = getString(R.string.veda_radio_stream_link_low)
                playAudio(url)
            }
            R.id.action_medium_quality -> {
                url = getString(R.string.veda_radio_stream_link_medium)
                playAudio(url)
            }
            R.id.action_high_quality -> {
                url = getString(R.string.veda_radio_stream_link_high)
                playAudio(url)
            }
            android.R.id.home -> {
                if (drawer_menu.isDrawerOpen(GravityCompat.START)) drawer_menu.closeDrawer(GravityCompat.START)
                 else drawer_menu.openDrawer(GravityCompat.START)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setUpActionBar() {
        val ab = supportActionBar
        ab?.apply {
            setHomeButtonEnabled(true)
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp)
        }
    }

    private fun playAudio(urlStream: String) {
        if (!serviceBound) {
            Log.d("MyLog", "StartService")
            val playerIntent = Intent(this, RadioPlayerService::class.java)
            playerIntent.putExtra(TAG_NEW_AUDIO_URL, urlStream)
            startService(playerIntent)
            bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE)
        } else {
            Log.d("MyLog", "service is already bound")
            val broadcastIntent: Intent = Intent(Broadcast_NEW_AUDIO)
            broadcastIntent.putExtra(TAG_NEW_AUDIO_URL, urlStream)
            sendBroadcast(broadcastIntent)
        }
    }


    private val broadcastStateServiceListener = object : BroadcastReceiverForPlayerService() {
        override fun onReceive(context: Context?, intent: Intent?) {
            STATE_OF_SERVICE_A = intent?.getSerializableExtra(TAG_STATE_SERVICE) as InitStatusMediaPlayer
        }
    }

    private fun registerBroadcastStateService() {
        val filter = IntentFilter(Broadcast_STATE_SERVICE)
        registerReceiver(broadcastStateServiceListener, filter)
    }

    private fun updateCheckGroupQuality(url: String) {
        if (myMenu == null) return
        when (url) {
            getString(R.string.veda_radio_stream_link_low) -> {
                myMenu?.findItem(R.id.action_low_quality)?.isChecked = true
                myMenu?.findItem(R.id.action_medium_quality)?.isChecked = false
                myMenu?.findItem(R.id.action_high_quality)?.isChecked = false
            }
            getString(R.string.veda_radio_stream_link_medium) -> {
                myMenu?.findItem(R.id.action_low_quality)?.isChecked = false
                myMenu?.findItem(R.id.action_medium_quality)?.isChecked = true
                myMenu?.findItem(R.id.action_high_quality)?.isChecked = false
            }
            getString(R.string.veda_radio_stream_link_high) -> {
                myMenu?.findItem(R.id.action_low_quality)?.isChecked = false
                myMenu?.findItem(R.id.action_medium_quality)?.isChecked = false
                myMenu?.findItem(R.id.action_high_quality)?.isChecked = true
            }
        }
    }
}