package ru.music.radiostationvedaradio.presenters

import android.util.Log
import ru.music.radiostationvedaradio.busines.ApiProvider
import ru.music.radiostationvedaradio.busines.repository.MainRepository
import ru.music.radiostationvedaradio.view.adapters.MainFragmentView
import ru.music.radiostationvedaradio.view.adapters.MainView

class MainPresenter : BasePresenter<MainView>() {

    private val repo = MainRepository(ApiProvider())

    override fun enable() {

    }

    fun refreshMainFragmentInfo(){
        viewState.setLoading(true)
        repo.reloadMetaData()
    }


}