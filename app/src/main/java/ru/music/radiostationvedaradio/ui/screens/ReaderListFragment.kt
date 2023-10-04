package ru.music.radiostationvedaradio.ui.screens


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import ru.music.radiostationvedaradio.R
import ru.music.radiostationvedaradio.ui.MainActivity


class ReaderListFragment : Fragment() {


    private lateinit var parentActivity: MainActivity


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view0 = inflater.inflate(R.layout.reader_list_fragment, container, false)
        parentActivity = activity as MainActivity
        view0.findViewById<Button>(R.id.btn_open_epub).setOnClickListener {
            // parentActivity.navController.navigate(R.id.viewPagerFragment)
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
                val action = ReaderListFragmentDirections.actionEpubReaderFragmentToMainFragment()
                findNavController().navigate(action)
            }
        }
        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, onBackPressedCallback)
    }

}