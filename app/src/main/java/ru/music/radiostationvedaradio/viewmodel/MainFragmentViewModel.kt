package ru.music.radiostationvedaradio.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.music.radiostationvedaradio.busines.ApiProvider
import ru.music.radiostationvedaradio.busines.repository.MainFragmentRepository

class MainFragmentViewModel(application: Application) : AndroidViewModel(application) {

    private val repo by lazy { MainFragmentRepository(ApiProvider()) }

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