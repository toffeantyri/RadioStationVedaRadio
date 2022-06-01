package ru.music.radiostationvedaradio.screens


import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import kotlinx.android.synthetic.main.bottom_player_panel.*
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.fragment_main.view.*
import ru.music.radiostationvedaradio.R
import ru.music.radiostationvedaradio.activityes.MainActivity
import ru.music.radiostationvedaradio.services.InitStatusMediaPlayer
import ru.music.radiostationvedaradio.utils.MyLog
import ru.music.radiostationvedaradio.utils.TAG
import ru.music.radiostationvedaradio.viewmodel.ViewModelMainActivity

class MainFragment : Fragment() {

    private val mViewModel: ViewModelMainActivity by activityViewModels()
    lateinit var parentActivity: MainActivity

    override fun onAttach(context: Context) {
        MyLog("MainFragment onAttach")
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view0 = inflater.inflate(R.layout.fragment_main, container, false)
        parentActivity = activity as MainActivity
        view0.apply {
            setUpOnClickStaticButton()
        }
        MyLog("MainFragment onCreateView")
        return view0
    }

    override fun onStart() {
        MyLog("MainFragment onStart")
        super.onStart()
        mViewModel.nounText.observe(this) {
            tv_tcitata_dnya.text = it
        }
        if (mViewModel.nounText.value.isNullOrEmpty()) loadNoun()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        MyLog("MainFragment onViewCreated")
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onResume() {
        MyLog("MainFragment onResume")
        mViewModel.statusMediaPlayer.observe(this) {
            if (it == InitStatusMediaPlayer.PLAYING) {
                main_equalizer?.animateBars()
                btn_panel_play.setImageResource(android.R.drawable.ic_media_pause)
            } else {
                main_equalizer?.stopBars()
                btn_panel_play.setImageResource(android.R.drawable.ic_media_play)
            }
        }
        super.onResume()
    }

    override fun onDestroy() {
        MyLog("MainFragment onDestroy")
        try {
            mViewModel.nounText.removeObservers(this)
        } catch (e: UninitializedPropertyAccessException) {
            Log.d(TAG, "${e.message}")
        }

        super.onDestroy()
    }

    override fun onDetach() {
        MyLog("MainFragment onDetach")
        super.onDetach()
    }

    private fun View.setUpOnClickStaticButton() {
        btn_refresh_tcitata.setOnClickListener {
            loadNoun()
        }
    }

    private fun loadNoun() {
        setNounLoading(true)
        mViewModel.refreshTodayNoun() {
            setNounLoading(false)
        }
    }

    private fun setNounLoading(visible: Boolean) = when (visible) {
        true -> noun_loading.visibility = View.VISIBLE
        false -> noun_loading.visibility = View.GONE
    }


}

