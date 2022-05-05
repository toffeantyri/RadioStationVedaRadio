package ru.music.radiostationvedaradio.presenters

import ru.music.radiostationvedaradio.view.adapters.MainView

class MainPresenter : BasePresenter<MainView>() {

    override fun enable() {

    }

    fun refreshMainFragmentInfo(){
        viewState.setLoading(true)
        //todo Обращение к репо
    }


}