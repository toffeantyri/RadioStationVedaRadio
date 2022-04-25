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
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
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
            web_view1.webViewClient = MyWebViewClient(this, context)
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


    class MyWebViewClient(private val rootView: View, context : Context) : WebViewClient(){
        private val context0 = context
        override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
            super.onReceivedError(view, request, error)
                Log.d("MyLog", "error loading webPage ${error?.description}")
            //Toast.makeText(context0, error?.description , Toast.LENGTH_SHORT).show()
        }

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            view?.visibility = View.VISIBLE
            rootView.progress_cicle_webpage.visibility = View.GONE
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
        }
    }
}
