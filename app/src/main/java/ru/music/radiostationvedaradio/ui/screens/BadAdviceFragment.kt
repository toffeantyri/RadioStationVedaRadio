package ru.music.radiostationvedaradio.ui.screens

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.music.radiostationvedaradio.R
import ru.music.radiostationvedaradio.databinding.BadAdviceFragmentBinding
import ru.music.radiostationvedaradio.ui.MainActivity
import ru.music.radiostationvedaradio.ui.adapters.badadvice.AntiHoroAdapter
import ru.music.radiostationvedaradio.ui.viewmodel.BadAdviceViewModel
import ru.music.radiostationvedaradio.utils.getTodayDate


class BadAdviceFragment : Fragment() {

    private lateinit var parentActivity: MainActivity
    private val viewModel: BadAdviceViewModel by viewModels()
    private val horoAdapter: AntiHoroAdapter = AntiHoroAdapter()
    private lateinit var binding: BadAdviceFragmentBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BadAdviceFragmentBinding.inflate(inflater)
        parentActivity = activity as MainActivity
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
                horoAdapter.fillListAdapter(it)
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
        binding.horoRv.adapter = horoAdapter
    }
}
