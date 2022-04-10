package ru.music.radiostationvedaradio

import android.app.ActionBar
import android.app.StatusBarManager
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.Window
import android.view.WindowManager
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    lateinit var radio: RadioClass

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setUpActionBar()
        radio = RadioClass(getString(R.string.veda_radio_stream_link))


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(radio.isPlaying() && item.itemId == R.id.action_play){
            radio.myPause()
            item.setIcon(R.drawable.ic_baseline_play_circle_filled_24)
        } else if(!radio.isPlaying() && item.itemId == R.id.action_play){
            radio.myPlay()
            item.setIcon(R.drawable.ic_baseline_pause_circle_filled_24)
        }

        return super.onOptionsItemSelected(item)
    }


    override fun onStop() {
        super.onStop()
        radio.myPause()
    }

    override fun onResume() {
        super.onResume()
    }


    private fun setUpActionBar() {
        val ab = supportActionBar
        ab?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_baseline_radio_24)
        }


    }
}