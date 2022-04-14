package ru.music.radiostationvedaradio.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ViewModelMainActivity : ViewModel() {

    val preparedStateComplete: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    init {
        preparedStateComplete.value = false
    }

    val stateIsPlaying: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    init {
        stateIsPlaying.value = false
    }

    val stateServiceBound : MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }
    init{
        stateServiceBound.value = false
    }

}