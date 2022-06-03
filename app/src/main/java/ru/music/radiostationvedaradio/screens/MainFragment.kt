package ru.music.radiostationvedaradio.screens


import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.fragment_main.view.*
import ru.music.radiostationvedaradio.R
import ru.music.radiostationvedaradio.activityes.MainActivity
import ru.music.radiostationvedaradio.utils.myLog
import ru.music.radiostationvedaradio.utils.TAG
import ru.music.radiostationvedaradio.viewmodel.ViewModelMainActivity

class MainFragment : Fragment() {

    private val mViewModel: ViewModelMainActivity by activityViewModels()
    lateinit var parentActivity: MainActivity

    override fun onAttach(context: Context) {
        myLog("MainFragment onAttach")
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
        myLog("MainFragment onCreateView")
        return view0
    }

    override fun onStart() {
        myLog("MainFragment onStart")
        super.onStart()
        mViewModel.nounText.observe(this) {
            tv_tcitata_dnya.text = it
        }
        if (mViewModel.nounText.value.isNullOrEmpty()) loadNoun()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        myLog("MainFragment onViewCreated")
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onResume() {
        myLog("MainFragment onResume")
        super.onResume()
    }

    override fun onDestroy() {
        myLog("MainFragment onDestroy")
        try {
            mViewModel.nounText.removeObservers(this)
        } catch (e: UninitializedPropertyAccessException) {
            Log.d(TAG, "${e.message}")
        }

        super.onDestroy()
    }

    override fun onDetach() {
        myLog("MainFragment onDetach")
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

