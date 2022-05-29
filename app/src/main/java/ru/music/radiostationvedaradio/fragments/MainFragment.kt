package ru.music.radiostationvedaradio.fragments


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_main.view.*
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import ru.music.radiostationvedaradio.R
import ru.music.radiostationvedaradio.presenters.MainFragmentPresenter
import ru.music.radiostationvedaradio.view.adapters.MainFragmentView

class MainFragment : MvpAppCompatFragment(), MainFragmentView {

    private val mainFragmentPresenter by moxyPresenter { MainFragmentPresenter() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view0 = inflater.inflate(R.layout.fragment_main, container, false)
        view0.apply {
            setUpOnClickStaticButton()
        }

        return view0
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainFragmentPresenter.enable()
        mainFragmentPresenter.refreshMainFragmentInfo()
    }

    private fun View.setUpOnClickStaticButton() {
        btn_refresh_tcitata.setOnClickListener {
            mainFragmentPresenter.refreshMainFragmentInfo()
        }
    }

    override fun showTodayNoun(data: String) {
        view?.tv_tcitata_dnya?.text = data
        Log.d("MyLogRx", "fragment: $data")
    }

    override fun setLoading(data: Boolean) {
    }
}

