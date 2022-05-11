package ru.music.radiostationvedaradio.presenters

import android.util.Log
import ru.music.radiostationvedaradio.busines.ApiProvider
import ru.music.radiostationvedaradio.busines.repository.MainFragmentRepository
import ru.music.radiostationvedaradio.view.adapters.MainFragmentView

class MainFragmentPresenter : BasePresenter<MainFragmentView>() {

    private val repo = MainFragmentRepository(ApiProvider())

    override fun enable() {
        repo.dataEmitter.subscribe { response  ->
            //Log.d("MyLogRx", "enable() dataEmitter.subscribe :" + response)
            viewState.setLoading(false)
            viewState.showTodayNoun(response)
        }
    }

    fun refreshMainFragmentInfo(){
        viewState.setLoading(true)
        repo.reloadNoun()
    }


}