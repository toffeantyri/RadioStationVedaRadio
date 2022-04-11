package ru.music.radiostationvedaradio

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModel
import ru.music.radiostationvedaradio.viewmodel.ViewModelMainActivity

const val TIME_UNTIL_ERROR_LOADING: Long = 10000
const val STATE_PREPARE_PREPARING = 0
const val STATE_PREPARE_COMPLITE = 1

class RadioClass(context0: Context, dataModel: ViewModelMainActivity, urlStream: String) {

    val context = context0
    private var handler: Handler? = null
    private val myDataModel = dataModel
    private val urlRadioStream: String = urlStream
    private var myMediaPlayer = newMediaPlayer(urlRadioStream)

    private fun newMediaPlayer(url: String): MediaPlayer {
        val mediaPlayer = MediaPlayer().apply {
            myDataModel.stateIsPlaing.value = false
            myDataModel.progressBarVisibility.value = View.GONE
            myDataModel.preparedStatement.value = STATE_PREPARE_PREPARING
            setAudioAttributes(
                AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA).build()
            )
            setDataSource(url)
            prepareAsync()
        }
        return mediaPlayer
    }

    fun myPlay() {
        myMediaPlayer.start()
        myDataModel.stateIsPlaing.value = myMediaPlayer.isPlaying
    }

    fun myReset() {
        myMediaPlayer.reset()
        myDataModel.preparedStatement.value = STATE_PREPARE_PREPARING
        myDataModel.stateIsPlaing.value = myMediaPlayer.isPlaying
        myDataModel.progressBarVisibility.value = View.GONE
    }

    fun myRelease() {
        myDataModel.preparedStatement.value = 0
        myMediaPlayer.release()
        myDataModel.stateIsPlaing.value = myMediaPlayer.isPlaying
    }

    fun myPause() {
        myMediaPlayer.pause()
        myDataModel.stateIsPlaing.value = myMediaPlayer.isPlaying
    }

    fun isPlaying(): Boolean {
        return myMediaPlayer.isPlaying
    }

    fun reloadRadio() {
        myReset()
        Toast.makeText(context, R.string.error_loading, Toast.LENGTH_SHORT).show()
        myMediaPlayer = newMediaPlayer(urlRadioStream)
    }

    fun setListenerLoadingAndPlayOrReloading() {
        handler = Handler()
        myDataModel.progressBarVisibility.value = View.VISIBLE
        myMediaPlayer.apply {
            if (myDataModel.preparedStatement.value == STATE_PREPARE_PREPARING) {
                    setOnPreparedListener {
                        myDataModel.preparedStatement.value = STATE_PREPARE_COMPLITE
                        myPlay()
                        myDataModel.progressBarVisibility.value = View.GONE
                    }
                    handler?.postDelayed({
                        if (myDataModel.preparedStatement.value == STATE_PREPARE_PREPARING && !myMediaPlayer.isPlaying) {
                            reloadRadio()
                            setListenerLoadingAndPlayOrReloading()
                        }
                    }, TIME_UNTIL_ERROR_LOADING)

            } else if (myDataModel.preparedStatement.value == STATE_PREPARE_COMPLITE) {
                myDataModel.progressBarVisibility.value = View.GONE
                myPlay()
            }

        }
    }


}
