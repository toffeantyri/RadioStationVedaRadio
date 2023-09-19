package ru.music.radiostationvedaradio.screens

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import ru.music.radiostationvedaradio.R
import ru.music.radiostationvedaradio.activityes.MainActivity
import ru.music.radiostationvedaradio.databinding.BadAdviceFragmentBinding
import ru.music.radiostationvedaradio.utils.getTodayDate
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
    private lateinit var binding: BadAdviceFragmentBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BadAdviceFragmentBinding.inflate(inflater)
        parentActivity = activity as MainActivity
        viewModel = ViewModelProvider(this).get(BadAdviceViewModel::class.java)
        setHasOptionsMenu(true)
        return binding.root
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
            if (it?.isNotEmpty() == true) {
                binding.tvDateList.text = it[0].date
                adapter.fillListAdapter(it)
            }
        }
    }


    private fun overrideOnBackPressedWithCallback() {
        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                Log.d("MyLog", "handleOnBackpressed")
                val action = BadAdviceFragmentDirections.actionBadAdviceFragmentToMainFragment()
                findNavController().navigate(action)
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

    private fun setLoading(visible: Boolean) {
        val horoProgressBar = view?.findViewById<ProgressBar>(R.id.horo_progressbar)
        when (visible) {
            true -> horoProgressBar?.visibility = View.VISIBLE
            false -> horoProgressBar?.visibility = View.GONE
        }
    }


    private fun initAntiHoroRv() {
        binding.horoRv.let {
            adapter = AntiHoroAdapter()
            myRecyclerView = it
            myRecyclerView.adapter = adapter
        }


    }
}
