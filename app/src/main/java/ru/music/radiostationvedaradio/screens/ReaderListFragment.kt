package ru.music.radiostationvedaradio.screens


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.reader_list_fragment.view.*
import ru.music.radiostationvedaradio.R
import ru.music.radiostationvedaradio.activityes.MainActivity
import ru.music.radiostationvedaradio.utils.navigateChangeTitleToolbar


class ReaderListFragment : Fragment() {

    companion object {
        fun newInstance() = ReaderListFragment()
    }

    private lateinit var parentActivity: MainActivity


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view0 = inflater.inflate(R.layout.reader_list_fragment, container, false)
        parentActivity = activity as MainActivity
        view0.btn_open_epub.setOnClickListener {
            parentActivity.navController.navigate(R.id.viewPagerFragment)
        }


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