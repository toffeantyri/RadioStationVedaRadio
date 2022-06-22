package ru.music.radiostationvedaradio.view.adapters

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import ru.music.radiostationvedaradio.R


const val ARG_TAB_POS = "arg tab position"

class PageFragment : Fragment() {


    private var onFragmentReadyListener: OnFragmentReadyListener? = null

    companion object {
        @JvmStatic
        fun newInstance(tabPosition: Int): PageFragment {
            val fragment = PageFragment()
            val bundle = Bundle()
            bundle.putInt(ARG_TAB_POS, tabPosition)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val rootView: View = inflater.inflate(R.layout.fragment_display, container, false)
        val mainLayout = rootView.findViewById<View>(R.id.fragment_main_layout) as RelativeLayout
        val argTabPosition = arguments?.getInt(ARG_TAB_POS) ?: 0
        val view = onFragmentReadyListener?.onFragmentReady(argTabPosition)
        if (view != null) {
            mainLayout.addView(view)
        }
        return rootView
    }


    fun PageFragment() {}

    interface OnFragmentReadyListener {
        fun onFragmentReady(position: Int): View?
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        onFragmentReadyListener = context as OnFragmentReadyListener
    }

    override fun onDestroy() {
        super.onDestroy()
        onFragmentReadyListener = null
    }


}