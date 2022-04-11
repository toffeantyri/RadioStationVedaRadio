package ru.music.radiostationvedaradio

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModel
import ru.music.radiostationvedaradio.viewmodel.ViewModelMainActivity

class RadioClass(dataModel: ViewModelMainActivity, urlStream: String) {

    private val myDataModel = dataModel
    private val urlRadioStream: String = urlStream
    private val myMediaPlayer = MediaPlayer().apply {
        myDataModel.progressBarVisibility.value = View.GONE
        myDataModel.preparedStatement.value = 0
        setAudioAttributes(
            AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setUsage(AudioAttributes.USAGE_MEDIA).build()
        )
        setDataSource(urlRadioStream)
        prepareAsync()
    }

    fun myPlay() {
        myMediaPlayer.start()

    }

    fun myPause() {
        myMediaPlayer.pause()
    }

    fun isPlaying(): Boolean {
        return myMediaPlayer.isPlaying
    }

    fun setLoadingListener() {
        myMediaPlayer.apply {
            if (myDataModel.preparedStatement.value == 0) {
                setOnPreparedListener {
                    myPlay()
                    myDataModel.progressBarVisibility.value = View.GONE
                    myDataModel.preparedStatement.value = 1

                }
            } else if (myDataModel.preparedStatement.value == 1) {
                myDataModel.progressBarVisibility.value = View.GONE
                myPlay()
            }
        }
    }


}
