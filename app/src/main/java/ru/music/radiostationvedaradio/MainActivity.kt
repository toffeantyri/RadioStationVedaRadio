package ru.music.radiostationvedaradio

import android.app.ActionBar
import android.app.StatusBarManager
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toolbar
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import kotlinx.android.synthetic.main.activity_main.*
import ru.music.radiostationvedaradio.viewmodel.ViewModelMainActivity

class MainActivity : AppCompatActivity() {

    lateinit var radio: RadioClass
    val dataModel: ViewModelMainActivity by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setUpActionBar()
        radio = RadioClass(this, dataModel, getString(R.string.veda_radio_stream_link))
        dataModel.progressBarVisibility.observe(this) {
            container_progressbar.visibility = it
        }

        dataModel.preparedStatement.observe(this) {
            Log.d("MyLog", "new preparedState: $it")
        }


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        val btnPlay = menu?.getItem(0)
        val btnReload = menu?.getItem(1)
        dataModel.stateIsPlaing.observe(this) {
            if (it) btnPlay?.setIcon(R.drawable.ic_baseline_pause_circle_filled_24) else if (!it) btnPlay?.setIcon(R.drawable.ic_baseline_play_circle_filled_24)
        }

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (radio.isPlaying() && item.itemId == R.id.action_play) {
            radio.myPause()
        } else if (!radio.isPlaying() && item.itemId == R.id.action_play) {
            radio.setListenerLoadingAndPlayOrReloading()
        } else if (item.itemId == R.id.action_stop_reset) {
            radio.myReset()
        }


        return super.onOptionsItemSelected(item)
    }


    override fun onStop() {
        super.onStop()
        radio.myPause()
    }

    override fun onDestroy() {
        radio.myRelease()
        super.onDestroy()
    }

    private fun setUpActionBar() {
        val ab = supportActionBar
        ab?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_baseline_radio_24)
        }
    }
}