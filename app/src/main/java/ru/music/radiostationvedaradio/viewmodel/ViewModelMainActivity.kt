package ru.music.radiostationvedaradio.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.music.radiostationvedaradio.busines.ApiProvider
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


    val nounText: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    fun refreshTodayNoun(onSuccess: () -> Unit) {
        repo.dataEmitter.subscribe {
            nounText.value = it
        }
        viewModelScope.launch(Dispatchers.Main) {
            repo.reloadNoun() {
                onSuccess()
            }
        }
    }

}