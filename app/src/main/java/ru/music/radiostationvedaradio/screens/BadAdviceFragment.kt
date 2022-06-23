package ru.music.radiostationvedaradio.screens

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.bad_advice_fragment.*
import kotlinx.android.synthetic.main.bad_advice_fragment.view.*
import ru.music.radiostationvedaradio.R
import ru.music.radiostationvedaradio.activityes.MainActivity
import ru.music.radiostationvedaradio.utils.getTodayDate
import ru.music.radiostationvedaradio.utils.myLogNet
import ru.music.radiostationvedaradio.utils.navigateChangeTitleToolbar
import ru.music.radiostationvedaradio.view.adapters.badadvice.AntiHoroAdapter
import ru.music.radiostationvedaradio.viewmodel.BadAdviceViewModel


class BadAdviceFragment : Fragment() {

    companion object {
        fun newInstance() = BadAdviceFragment()
    }

    private lateinit var parentActivity: MainActivity
    private lateinit var viewModel: BadAdviceViewModel
    private lateinit var myRecyclerView: RecyclerView
    private lateinit var adapter: AntiHoroAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view0 = inflater.inflate(R.layout.bad_advice_fragment, container, false)
        parentActivity = activity as MainActivity
        viewModel = ViewModelProvider(this).get(BadAdviceViewModel::class.java)
        setHasOptionsMenu(true)
        return view0
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        overrideOnBackPressedWithCallback()
    }

    override fun onStart() {
        super.onStart()
        initAntiHoroRv()
        if (viewModel.listHoroOfToday.value.isNullOrEmpty()) loadHoroscope()


    }

    override fun onResume() {
        super.onResume()

        viewModel.listHoroOfToday.observe(viewLifecycleOwner) {
            it.forEach { myLogNet("BAFRAG : observe: " + it) }
            if (it.isNotEmpty()) {
                view?.tv_date_list?.text = it[0].date
                adapter.fillListAdapter(it)
            }
        }
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


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        (parentActivity).mToolbar.title = ""
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


    fun initAntiHoroRv() {
        adapter = AntiHoroAdapter()
        myRecyclerView = horo_rv
        myRecyclerView.adapter = adapter

    }
}
