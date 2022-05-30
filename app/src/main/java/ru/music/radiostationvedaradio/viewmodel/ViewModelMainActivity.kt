package ru.music.radiostationvedaradio.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.music.radiostationvedaradio.busines.ApiProvider
import ru.music.radiostationvedaradio.busines.repository.MainFragmentRepository
import ru.music.radiostationvedaradio.busines.repository.MainRepository
import ru.music.radiostationvedaradio.services.InitStatusMediaPlayer

class ViewModelMainActivity(application : Application) : AndroidViewModel(application) {

    private val repo by lazy { MainRepository(ApiProvider()) }

    val statusMediaPlayer: MutableLiveData<InitStatusMediaPlayer> by lazy {
        MutableLiveData<InitStatusMediaPlayer>()
    }

    init {
        statusMediaPlayer.value = InitStatusMediaPlayer.IDLE
    }


}