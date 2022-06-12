package ru.music.radiostationvedaradio.screens

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.github.mertakdut.Reader
import ru.music.radiostationvedaradio.R
import ru.music.radiostationvedaradio.activityes.MainActivity
import ru.music.radiostationvedaradio.utils.navigateChangeTitleToolbar
import ru.music.radiostationvedaradio.view.adapters.PageFragment


class ViewPagerFragment : Fragment() {

    companion object {
        @JvmStatic
        fun newInstance() {
        }
    }

    private lateinit var parentActivity: MainActivity
    private lateinit var reader: Reader
    private lateinit var viewPager: ViewPager
    private lateinit var mSectionPager: SectionsPagerAdapter
    private var pxScreenWidth: Int = 0
    private val pageCount = Int.MAX_VALUE


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view0 = inflater.inflate(R.layout.fragment_view_pager, container, false)
        parentActivity = activity as MainActivity
        overrideOnBackPressedWithCallback()



        return view0
    }


    private fun overrideOnBackPressedWithCallback() {
        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                Log.d("MyLog", "handleOnBackpressed")
                parentActivity.apply {
                    navController.navigateChangeTitleToolbar(
                        parentActivity,
                        R.id.action_viewPagerFragment_to_epubReaderFragment
                    )
                }
            }
        }
        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, onBackPressedCallback)
    }


    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

        override fun getCount(): Int {
            return pageCount
        }

        override fun getItem(position: Int): Fragment {
            // getItem is called to instantiate the fragment for the given page.
            return PageFragment.newInstance(position)
        }
    }
}


