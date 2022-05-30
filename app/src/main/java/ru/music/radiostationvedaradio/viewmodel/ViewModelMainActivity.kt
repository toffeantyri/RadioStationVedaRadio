package ru.music.radiostationvedaradio.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.music.radiostationvedaradio.services.InitStatusMediaPlayer

class ViewModelMainActivity : ViewModel() {

    val statusMediaPlayer: MutableLiveData<InitStatusMediaPlayer> by lazy {
        MutableLiveData<InitStatusMediaPlayer>()
    }

    init {
        statusMediaPlayer.value = InitStatusMediaPlayer.IDLE
    }


}