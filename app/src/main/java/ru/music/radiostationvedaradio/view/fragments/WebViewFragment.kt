package ru.music.radiostationvedaradio.view.fragments

import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Message
import android.provider.Settings.Global.getString
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.fragment.app.activityViewModels
import kotlinx.android.synthetic.main.fragment_web_view.*
import kotlinx.android.synthetic.main.fragment_web_view.view.*
import ru.music.radiostationvedaradio.R
import ru.music.radiostationvedaradio.viewmodel.ViewModelMainActivity

const val TAG_WEB_URL = "web_url"

class WebViewFragment : Fragment() {

    private val dataModel: ViewModelMainActivity by activityViewModels()
    lateinit var webUrl: String


    companion object {
        @JvmStatic
        fun newInstance(urlSite: String) = WebViewFragment().apply {
            val args = Bundle()
            args.putString(TAG_WEB_URL, urlSite)
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
        webUrl = arguments?.getString(TAG_WEB_URL) ?: ""



        return view0
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onBackPressed(view.web_view1)
        dataModel.statusFragmentConnected.value = true

        view.apply {
            progress_cicle_webpage.visibility = View.VISIBLE
            web_view1.apply {
                webViewClient = WebClientForFragment(view)
                webChromeClient = WebChromeClientForFragment(view)
                settings.javaScriptEnabled = true
                settings.apply {
                    setSupportZoom(true)
                    builtInZoomControls = true
                }
                loadUrl(webUrl)
            }
        }

    }





    private fun onBackPressed(webView : WebView) {
        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if(webView.canGoBack()){
                    webView.goBack()
                } else {
                    dataModel.statusFragmentConnected.value = false
                    activity?.supportFragmentManager?.beginTransaction()?.remove(this@WebViewFragment)?.commit()
                }
            }
        }
        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner,onBackPressedCallback)
    }

}
