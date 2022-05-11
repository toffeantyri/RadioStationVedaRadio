package ru.music.radiostationvedaradio.view.adapters

import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEnd
import moxy.viewstate.strategy.alias.AddToEndSingle

interface MainFragmentView : MvpView {

    @AddToEnd
    fun showTodayNoun(data : String)

    @AddToEndSingle
    fun setLoading(data : Boolean)


}