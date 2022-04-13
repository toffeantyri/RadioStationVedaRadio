package ru.music.radiostationvedaradio.view

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.media.AudioManager
import android.os.Bundle
import android.os.IBinder
import android.os.PersistableBundle
import android.util.Log
import android.view.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.createBitmap
import kotlinx.android.synthetic.main.action_progressbar.*
import kotlinx.android.synthetic.main.activity_main.*
import ru.music.radiostationvedaradio.BroadcastReceiverForPlayerSevice
import ru.music.radiostationvedaradio.R
import ru.music.radiostationvedaradio.services.RadioPlayerService
import ru.music.radiostationvedaradio.viewmodel.ViewModelMainActivity
import java.util.zip.Inflater

class MainActivity : AppCompatActivity() {

    var serviceBound = false
    var mediaService: RadioPlayerService? = null
    val dataModel: ViewModelMainActivity by viewModels()
    lateinit var url: String
    lateinit var btnPlay : MenuItem

    private val serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as RadioPlayerService.LocalBinder
            mediaService = binder.getService(dataModel)
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

        url = getString(R.string.veda_radio_stream_link)
        Log.d("MyLog", "mediaservice: " + mediaService.toString() + "serverBound: $serviceBound")




    }

    override fun onStart() {
        super.onStart()
        playAudio(url)
    }

    override fun onResume() {
        super.onResume()
        volumeControlStream = AudioManager.STREAM_MUSIC
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        outState.putBoolean("ServiceState", serviceBound)
        super.onSaveInstanceState(outState, outPersistentState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        serviceBound = savedInstanceState.getBoolean("ServiceState")
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
        btnPlay = menu?.getItem(0)!!
        dataModel.stateIsPlaying.observe(this) {
            if (it) btnPlay?.setIcon(R.drawable.ic_baseline_pause_circle_filled_24)
            if (!it) btnPlay?.setIcon(R.drawable.ic_baseline_play_circle_filled_24)
        }
        dataModel.preparedStateComplete.observe(this){
            if (it) {
                btnPlay.collapseActionView()
                btnPlay.actionView = null
            } else {
                btnPlay.setActionView(R.layout.action_progressbar)
                btnPlay.expandActionView()
            }
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_play && mediaService != null) {
            when {
                serviceBound && !mediaService!!.isPlaying() -> mediaService?.playMedia()
                serviceBound && mediaService!!.isPlaying() -> mediaService?.pauseMedia()
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
            val playerIntent = Intent(this, RadioPlayerService::class.java)
            playerIntent.putExtra("url", urlStream)
            startService(playerIntent)
            bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE)
        } else {
            Log.d("MyLog", "service is already bound")
            //todo send media with broadcastReceiver
        }
    }

}