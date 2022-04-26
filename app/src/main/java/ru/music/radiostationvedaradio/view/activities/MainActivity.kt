package ru.music.radiostationvedaradio.view.activities

import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.core.view.GravityCompat
import kotlinx.android.synthetic.main.activity_main.*
import ru.music.radiostationvedaradio.R
import ru.music.radiostationvedaradio.services.*

class MainActivity : BaseMainActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setUpActionBar()
        url = getString(R.string.veda_radio_stream_link_low) // TODO Качество по умолчанию на релиз - MEDIUM
        webUrl = getString(R.string.veda_radio_site)
        registerBroadcastStateService()
        playAudio(url)
        loadAndShowBanner()

        dataModel.statusFragmentConnected.observe(this){
            fragmentIsConnected = it
        }

    }

    override fun onPause() {
        super.onPause()
        Log.d("MyLog", "MainActivity onPause")
    }

    override fun onStart() {
        super.onStart()
        draw_navView.setUpDrawerNavViewListener()
        Log.d("MyLog", "MainActivity onStart")
    }

    override fun onResume() {
        super.onResume()
        volumeControlStream = AudioManager.STREAM_MUSIC
    }

    override fun onDestroy() {
        main_banner.destroy()
        if (serviceBound) {
            unbindService(serviceConnection)
        }
        super.onDestroy()
    }

    private fun playAudio(urlStream: String) {
        if (!serviceBound) {
            Log.d("MyLog", "StartService")
            val playerIntent = Intent(this@MainActivity, RadioPlayerService::class.java)
            if (this.isServiceRunning(RadioPlayerService::class.java)) {
                playerIntent.putExtra(TAG_FIRST_RUN, false)
            } else {
                playerIntent.putExtra(TAG_FIRST_RUN, true)
                playerIntent.putExtra(TAG_NEW_AUDIO_URL, urlStream)
            }
            this@MainActivity.startForegroundService(playerIntent)
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


}