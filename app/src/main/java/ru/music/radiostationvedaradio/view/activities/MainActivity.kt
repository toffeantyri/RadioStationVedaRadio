package ru.music.radiostationvedaradio.view.activities

import android.media.AudioManager
import android.os.Bundle
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import ru.music.radiostationvedaradio.R


class MainActivity : BaseMainActivity() {

    private var job: Job? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        url = getString(R.string.veda_radio_stream_link_low) // TODO Качество по умолчанию на релиз - MEDIUM


        job = CoroutineScope(Dispatchers.IO).launch {
            Log.d("MyLog", "Coroutine job : $job")
            initExpandableListInNavView()
            initListViewInNavView()
            setUpActionBar()
            registerBroadcastStateService()
            loadAndShowBanner()
        }
        job?.invokeOnCompletion {
            Log.d("MyLog", "invokeOnComplition Job")
        }


        playAudio(url)
        dataModel.statusFragmentConnected.observe(this) {
            fragmentIsConnected = it
            if (it) {
                container_frame_for_website.visibility = View.VISIBLE
                supportActionBar?.hide()
            } else {
                container_frame_for_website.visibility = View.GONE
                supportActionBar?.show()
            }

        }
    }

    override fun onPause() {
        super.onPause()
        Log.d("MyLog", "MainActivity onPause")
    }

    override fun onStart() {
        super.onStart()


        Log.d("MyLog", "MainActivity onStart")
    }

    override fun onResume() {
        super.onResume()
        volumeControlStream = AudioManager.STREAM_MUSIC
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        main_banner.destroy()
        if (serviceBound) {
            unbindService(serviceConnection)
        }
        super.onDestroy()
    }


}