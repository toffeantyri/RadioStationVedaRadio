package ru.music.radiostationvedaradio.view

import android.content.*
import android.media.AudioManager
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import ru.music.radiostationvedaradio.R
import ru.music.radiostationvedaradio.services.BroadcastReceiverForPlayerService
import ru.music.radiostationvedaradio.services.InitStatusMediaPlayer
import ru.music.radiostationvedaradio.services.RadioPlayerService
import ru.music.radiostationvedaradio.viewmodel.ViewModelMainActivity

const val Broadcast_NEW_AUDIO = "ru.music.vedaradio.NEW_AUDIO"
const val Broadcast_StateService = "ru.music.vedaradio.STATE_SERVICE"

class MainActivity : AppCompatActivity() {

    val dataModel: ViewModelMainActivity by viewModels()

    var serviceBound = false
        set(value) {
            field = value
            Log.d("MyLog", "serviceBound -> $value")
        }
    var mediaService: RadioPlayerService? = null

    lateinit var url: String

    lateinit var btnPlay: MenuItem
    lateinit var btnRefresh: MenuItem

    private val serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as RadioPlayerService.LocalBinder
            mediaService = binder.getService(dataModel)
            serviceBound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            serviceBound = false
            Log.d("MyLog", "onServiceDisconnected")
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setUpActionBar()
        url = getString(R.string.veda_radio_stream_link)
        registerBroadcastStateService()
        dataModel.statusMediaPlayer.value = InitStatusMediaPlayer.INITIALISATION
        playAudio(url)
    }

    override fun onPause() {
        super.onPause()
        Log.d("MyLog", "Mainactivity onPause")
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
        btnPlay = menu?.getItem(1)!!
        btnRefresh = menu.getItem(0)!!
        dataModel.stateIsPlaying.observe(this) {
            if (it) btnPlay.setIcon(R.drawable.ic_baseline_pause_circle_filled_24)
            if (!it) btnPlay.setIcon(R.drawable.ic_baseline_play_circle_filled_24)
        }

        dataModel.statusMediaPlayer.observe(this) {
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
            Log.d("MyLog", "action_play click. isPlaying: ${mediaService!!.isPlaying()}")
            when (dataModel.statusMediaPlayer.value) {
                InitStatusMediaPlayer.INIT_COMPLETE -> {
                    when {
                        serviceBound && !mediaService!!.isPlaying() -> mediaService?.playMedia()
                        serviceBound && mediaService!!.isPlaying() -> mediaService?.pauseMedia()
                    }
                }
                InitStatusMediaPlayer.IDLE -> {
                    if (serviceBound) playAudio(url)
                }
                InitStatusMediaPlayer.INITIALISATION -> {
                    Toast.makeText(this, "Loading", Toast.LENGTH_SHORT).show()
                }
            }
        }
        if(item.itemId == R.id.action_refresh) {
            playAudio(url)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setUpActionBar() {
        val ab = supportActionBar
        ab?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_baseline_radio_24)
        }
    }

    private fun playAudio(urlStream: String) {
        if (!serviceBound) {
            Log.d("MyLog", "StartService")
            val playerIntent = Intent(this, RadioPlayerService::class.java)
            playerIntent.putExtra("url", urlStream)
            startService(playerIntent)
            bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE)
        } else {
            Log.d("MyLog", "service is already bound")
            val broadcastIntent: Intent = Intent(Broadcast_NEW_AUDIO)
            broadcastIntent.putExtra("url", urlStream)
            sendBroadcast(broadcastIntent)
        }
    }


    private val broadcastStateServiceListener = object : BroadcastReceiverForPlayerService() {
        override fun onReceive(context: Context?, intent: Intent?) {

        }
    }

    private fun registerBroadcastStateService() {
        val filter = IntentFilter(Broadcast_StateService)
        registerReceiver(broadcastStateServiceListener, filter)
    }
}