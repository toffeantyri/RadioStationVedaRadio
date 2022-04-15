package ru.music.radiostationvedaradio.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.music.radiostationvedaradio.services.InitStatusMediaPlayer

class ViewModelMainActivity : ViewModel() {

    val stateIsPlaying: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }
    init {
        stateIsPlaying.value = false
    }


    val statusMediaPlayer : MutableLiveData<InitStatusMediaPlayer> by lazy {
        MutableLiveData<InitStatusMediaPlayer>()
    }
    init {
        statusMediaPlayer.value = InitStatusMediaPlayer.IDLE
    }


}