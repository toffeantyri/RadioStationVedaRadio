package ru.music.radiostationvedaradio.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.music.radiostationvedaradio.busines.api.ApiProvider
import ru.music.radiostationvedaradio.busines.repository.BadAdviceReposotory

class BadAdviceViewModel(application: Application) : AndroidViewModel(application) {

    private val repo by lazy { BadAdviceReposotory(ApiProvider()) }


    val listHoroOfToday : MutableLiveData<List<String>> by lazy {
        MutableLiveData<List<String>>()
    }


    fun refreshTodayAntiHoroscope(date : String, onSuccess : () -> Unit){
        repo.dataEmitter.subscribe{
            listHoroOfToday.value = it
        }
        viewModelScope.launch(Dispatchers.IO) {
            repo.loadNewHoro(date){
                onSuccess()
            }
        }
    }

}

