package ru.music.radiostationvedaradio.screens

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.bad_advice_fragment.*
import kotlinx.android.synthetic.main.bad_advice_fragment.view.*
import kotlinx.android.synthetic.main.fragment_main.*
import ru.music.radiostationvedaradio.viewmodel.BadAdviceViewModel
import ru.music.radiostationvedaradio.R
import ru.music.radiostationvedaradio.activityes.MainActivity
import ru.music.radiostationvedaradio.utils.getTodayDate
import ru.music.radiostationvedaradio.utils.myLog
import ru.music.radiostationvedaradio.utils.myLogNet
import ru.music.radiostationvedaradio.utils.navigateChangeTitleToolbar
import java.text.SimpleDateFormat
import java.util.*


class BadAdviceFragment : Fragment() {

    companion object {
        fun newInstance() = BadAdviceFragment()
    }

    private lateinit var parentActivity: MainActivity
    private lateinit var viewModel: BadAdviceViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view0 = inflater.inflate(R.layout.bad_advice_fragment, container, false)
        parentActivity = activity as MainActivity
        viewModel = ViewModelProvider(this).get(BadAdviceViewModel::class.java)
        return view0
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.listHoroOfToday.observe(this) {
            it.forEach { myLogNet("BAFRAG : observe: " + it) }
            view.tv_date_list.text = it[0]
        }

        overrideOnBackPressedWithCallback()
    }

    override fun onStart() {
        super.onStart()
        if (viewModel.listHoroOfToday.value.isNullOrEmpty()) loadHoroscope()
    }


    private fun overrideOnBackPressedWithCallback() {
        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                Log.d("MyLog", "handleOnBackpressed")
                parentActivity.apply {
                    navController.navigateChangeTitleToolbar(
                        parentActivity,
                        R.id.action_badAdviceFragment_to_mainFragment
                    )
                }
            }
        }
        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, onBackPressedCallback)
    }


    private fun loadHoroscope() {
        val date = getTodayDate("dd.MM.yyyy")
        setLoading(true)
        viewModel.refreshTodayAntiHoroscope(date) {
            setLoading(false)
        }
    }

    private fun setLoading(visible: Boolean) = when (visible) {
        true -> horo_progressbar.visibility = View.VISIBLE
        false -> horo_progressbar.visibility = View.GONE
    }

}
