package ru.music.radiostationvedaradio.screens

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import ru.music.radiostationvedaradio.viewmodel.BadAdviceViewModel
import ru.music.radiostationvedaradio.R
import ru.music.radiostationvedaradio.activityes.MainActivity
import ru.music.radiostationvedaradio.utils.navigateChangeTitleToolbar


class BadAdviceFragment : Fragment() {

    companion object {
        fun newInstance() = BadAdviceFragment()
    }

    private lateinit var parentActivity : MainActivity
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
        overrideOnBackPressedWithCallback()
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

}
