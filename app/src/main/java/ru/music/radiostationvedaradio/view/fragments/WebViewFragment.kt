package ru.music.radiostationvedaradio.view.fragments

import android.os.Bundle
import android.os.Message
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.fragment.app.activityViewModels
import kotlinx.android.synthetic.main.fragment_web_view.*
import kotlinx.android.synthetic.main.fragment_web_view.view.*
import ru.music.radiostationvedaradio.R
import ru.music.radiostationvedaradio.viewmodel.ViewModelMainActivity


class WebViewFragment : Fragment() {

    private val dataModel: ViewModelMainActivity by activityViewModels()
    lateinit var webUrl: String


    companion object {
        @JvmStatic
        fun newInstance(urlSite: String) = WebViewFragment().apply {
            val args = Bundle()
            args.putString("web_url", urlSite)
            arguments = args
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view0 = inflater.inflate(R.layout.fragment_web_view, container, false)
        webUrl = arguments?.getString("web_url") ?: ""
        view0.apply {
            web_view1.webViewClient = WebViewClient()
            web_view1.settings.javaScriptEnabled = true
            web_view1.loadUrl(webUrl)
        }

        return view0
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onBackPressed(view.web_view1)
        dataModel.statusFragmentConnected.value = true

    }

    private fun onBackPressed(webView : WebView) {
        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if(webView.canGoBack()){
                    webView.goBack()
                } else {
                    activity?.supportFragmentManager?.beginTransaction()?.remove(this@WebViewFragment)?.commit()
                    dataModel.statusFragmentConnected.value = false
                }
            }
        }
        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner,onBackPressedCallback)
    }
}
