package ru.music.radiostationvedaradio.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.music.radiostationvedaradio.R
import ru.music.radiostationvedaradio.data.api.ApiProvider
import ru.music.radiostationvedaradio.data.model.MetadataRadioService
import ru.music.radiostationvedaradio.data.repository.MainRepository
import ru.music.radiostationvedaradio.services.InitStatusMediaPlayer

class ViewModelMainActivity(private val application: Application) : AndroidViewModel(application) {

    private val repo by lazy { MainRepository(ApiProvider()) }

    val statusMediaPlayer: MutableLiveData<InitStatusMediaPlayer> by lazy {
        MutableLiveData<InitStatusMediaPlayer>(InitStatusMediaPlayer.IDLE)
    }

    val playingUrl: MutableLiveData<String> by lazy {
        MutableLiveData(application.getString(R.string.veda_radio_stream_link_low))
    }

    val metadataOfPlayer: MutableLiveData<MetadataRadioService> by lazy {
        MutableLiveData<MetadataRadioService>(MetadataRadioService("Veda Radio", "Veda Radio"))
    }

    val nounText: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    fun getPlayingUrl(): String {
        return playingUrl.value ?: ""
    }

    fun refreshTodayNoun(onSuccess: () -> Unit) {
        viewModelScope.launch(Dispatchers.Main) {
            repo.dataEmitter.collect {
                nounText.value = it
            }
        }
        repo.reloadNoun() {
            onSuccess()
        }
    }

}