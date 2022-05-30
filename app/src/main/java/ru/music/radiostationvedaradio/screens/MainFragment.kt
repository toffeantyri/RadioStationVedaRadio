package ru.music.radiostationvedaradio.screens


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.fragment_main.view.*
import ru.music.radiostationvedaradio.R
import ru.music.radiostationvedaradio.viewmodel.MainFragmentViewModel

class MainFragment : Fragment() {

    private lateinit var mViewModel: MainFragmentViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view0 = inflater.inflate(R.layout.fragment_main, container, false)
        mViewModel = ViewModelProvider(this).get(MainFragmentViewModel::class.java)
        mViewModel.nounText.observe(this){
            tv_tcitata_dnya.text = it
        }

        view0.apply {
            setUpOnClickStaticButton()
        }

        return view0
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    private fun View.setUpOnClickStaticButton() {
        btn_refresh_tcitata.setOnClickListener {
            setNounLoading(true)
            mViewModel.refreshTodayNoun() {
                setNounLoading(false)
            }
        }
    }

    private fun setNounLoading(visible: Boolean) = when (visible) {
        true -> noun_loading.visibility = View.VISIBLE
        false -> noun_loading.visibility = View.GONE
    }


}

