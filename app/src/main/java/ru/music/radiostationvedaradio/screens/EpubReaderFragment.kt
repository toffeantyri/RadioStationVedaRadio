package ru.music.radiostationvedaradio.screens

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import ru.music.radiostationvedaradio.R
import ru.music.radiostationvedaradio.activityes.MainActivity
import ru.music.radiostationvedaradio.utils.navigateChangeTitleToolbar


class EpubReaderFragment : Fragment() {

    companion object {
        fun newInstance() = EpubReaderFragment()
    }

    private lateinit var parentActivity : MainActivity


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view0 = inflater.inflate(R.layout.epub_reader_fragment, container, false)
        parentActivity = activity as MainActivity
            overrideOnBackPressedWithCallback()
        return view0
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

    }

    private fun overrideOnBackPressedWithCallback() {
        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                Log.d("MyLog", "handleOnBackpressed")
                parentActivity.apply {
                    navController.navigateChangeTitleToolbar(
                        parentActivity,
                        R.id.action_epubReaderFragment_to_mainFragment
                    )
                }
            }
        }
        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, onBackPressedCallback)
    }

}