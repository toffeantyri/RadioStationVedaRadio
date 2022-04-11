package ru.music.radiostationvedaradio.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ViewModelMainActivity : ViewModel() {

    val progressBarVisibility : MutableLiveData<Int> by lazy {
        MutableLiveData<Int>()
    }
    val preparedStatement : MutableLiveData<Int> by lazy {
        MutableLiveData<Int>()
    }

}