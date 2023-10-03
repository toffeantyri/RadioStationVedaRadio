package ru.music.radiostationvedaradio.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.music.radiostationvedaradio.data.api.ApiProvider
import ru.music.radiostationvedaradio.data.repository.MainRepository

class ViewModelMainActivity(private val application: Application) : AndroidViewModel(application) {

    private val repo by lazy { MainRepository(ApiProvider()) }

    val nounText: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
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