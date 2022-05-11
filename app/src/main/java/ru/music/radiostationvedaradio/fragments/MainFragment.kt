package ru.music.radiostationvedaradio.fragments


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.fragment_main.view.*
import kotlinx.coroutines.*
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory

import ru.music.radiostationvedaradio.R
import ru.music.radiostationvedaradio.busines.data_main_tcitata.EncodingInterceptor
import ru.music.radiostationvedaradio.busines.data_main_tcitata.HareKrishnaService
import ru.music.radiostationvedaradio.presenters.MainFragmentPresenter
import ru.music.radiostationvedaradio.presenters.MainPresenter
import ru.music.radiostationvedaradio.view.adapters.MainFragmentView
import ru.music.radiostationvedaradio.view.adapters.MainView

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
        mainFragmentPresenter.enable()
        return view0
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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

