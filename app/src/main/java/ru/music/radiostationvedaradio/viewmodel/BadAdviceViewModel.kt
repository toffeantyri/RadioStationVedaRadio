package ru.music.radiostationvedaradio.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.music.radiostationvedaradio.data.api.ApiProvider
import ru.music.radiostationvedaradio.data.model.antihoro.HoroItemHolder
import ru.music.radiostationvedaradio.data.repository.BadAdviceReposotory

class BadAdviceViewModel(application: Application) : AndroidViewModel(application) {

    private val repo by lazy { BadAdviceReposotory(ApiProvider()) }


    val listHoroOfToday: MutableLiveData<List<HoroItemHolder>> by lazy {
        MutableLiveData<List<HoroItemHolder>>()
    }


    fun refreshTodayAntiHoroscope(date: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            repo.dataEmitter.collect {
                listHoroOfToday.value = it
            }
        }

        viewModelScope.launch(Dispatchers.IO) {
            repo.loadFromDatabaseAndCheckDate(date, { onSuccess() }) {
                repo.loadNewHoro() {
                    onSuccess()
                }
            }
        }
    }

}

