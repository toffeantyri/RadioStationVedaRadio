package ru.music.radiostationvedaradio.view

import android.content.*
import android.media.AudioManager
import android.os.Bundle
import android.os.IBinder
import android.os.PersistableBundle
import android.util.Log
import android.view.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import ru.music.radiostationvedaradio.R
import ru.music.radiostationvedaradio.services.RadioPlayerService
import ru.music.radiostationvedaradio.viewmodel.ViewModelMainActivity

const val Broadcast_NEW_AUDIO = "ru.music.vedaradio.NEW_AUDIO"

class MainActivity : AppCompatActivity() {

    var serviceBound = false
    var mediaService: RadioPlayerService? = null
    val dataModel: ViewModelMainActivity by viewModels()
    lateinit var url: String
    lateinit var btnPlay: MenuItem
    lateinit var btnNone: MenuItem

    private val serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as RadioPlayerService.LocalBinder
            mediaService = binder.getService(dataModel)
            dataModel.stateServiceBound.value = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            dataModel.stateServiceBound.value = false
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setUpActionBar()
        url = getString(R.string.veda_radio_stream_link)

        dataModel.stateServiceBound.observe(this) {
            serviceBound = it
            Log.d("MyLog", "serviceBount = $serviceBound")
        }

        playAudio(url)
    }

    override fun onPause() {
        super.onPause()
        Log.d("MyLog", "Mainactivity onPause")
    }

    override fun onResume() {
        super.onResume()

        volumeControlStream = AudioManager.STREAM_MUSIC
        Log.d("MyLog", "MainActivity onResume")
    }

    override fun onDestroy() {
        super.onDestroy()
        if (serviceBound) {
            unbindService(serviceConnection)
            mediaService?.stopSelf()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        btnPlay = menu?.getItem(1)!!
        btnNone = menu.getItem(0)!!
        dataModel.stateIsPlaying.observe(this) {
            if (it) btnPlay.setIcon(R.drawable.ic_baseline_pause_circle_filled_24)
            if (!it) btnPlay.setIcon(R.drawable.ic_baseline_play_circle_filled_24)
        }
        dataModel.preparedStateComplete.observe(this) {
            if (it) {
                btnNone.collapseActionView()
                btnNone.actionView = null
            } else {
                btnNone.setActionView(R.layout.action_progressbar)
                btnNone.expandActionView()
            }
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_play && mediaService != null) {
            Log.d("MyLog", "action_play click. isPlaying: ${mediaService!!.isPlaying()}")
            if (dataModel.preparedStateComplete.value!!) {
                when {
                    serviceBound && !mediaService!!.isPlaying() -> mediaService?.playMedia()
                    serviceBound && mediaService!!.isPlaying() -> mediaService?.pauseMedia()
                }
            }

            else if (!dataModel.preparedStateComplete.value!!) {
                when {
                    serviceBound  -> playAudio(url)
                }
            }

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
            val broadcastIntent : Intent = Intent(Broadcast_NEW_AUDIO)
            sendBroadcast(broadcastIntent)
        }
    }

}