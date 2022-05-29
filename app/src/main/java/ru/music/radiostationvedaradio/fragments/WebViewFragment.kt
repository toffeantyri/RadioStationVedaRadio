package ru.music.radiostationvedaradio.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.activity.OnBackPressedCallback
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import kotlinx.android.synthetic.main.fragment_web_view.view.*
import ru.music.radiostationvedaradio.R
import ru.music.radiostationvedaradio.activityes.MainActivity
import ru.music.radiostationvedaradio.busines.WebChromeClientForFragment
import ru.music.radiostationvedaradio.busines.WebClientForFragment
import ru.music.radiostationvedaradio.utils.APP_CONTEXT
import ru.music.radiostationvedaradio.viewmodel.ViewModelMainActivity

const val TAG_WEB_URL = "web_url"

class WebViewFragment : Fragment() {

    private val dataModel: ViewModelMainActivity by activityViewModels()
    lateinit var webUrl: String


    companion object {
        @JvmStatic
        fun newInstance() = WebViewFragment().apply {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view0 = inflater.inflate(R.layout.fragment_web_view, container, false)
        webUrl = arguments?.getString(TAG_WEB_URL) ?: ""
        setHasOptionsMenu(true)
        return view0
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onBackPressed(view.web_view1)

        view.apply {
            progress_cicle_webpage.visibility = View.VISIBLE
            web_view1.apply {
                webViewClient = WebClientForFragment(view)
                webChromeClient = WebChromeClientForFragment(view)
                settings.apply {
                    setSupportZoom(true)

                    javaScriptEnabled = true
                    allowFileAccess = false

                    builtInZoomControls = true
                    domStorageEnabled = true
                    loadWithOverviewMode = true
                    useWideViewPort = true
                    defaultTextEncodingName = "utf-8"
                    setAppCacheEnabled(true)
                    loadsImagesAutomatically = true
                    mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                }
                setBackgroundColor(resources.getColor(R.color.green_100_alp50))
                loadUrl(webUrl)
            }
        }

    }





    private fun onBackPressed(webView: WebView) {
        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                Log.d("MyLog", "handleOnBackpressed" )
                if (webView.canGoBack()) {
                    webView.goBack()
                }
            }
        }
        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, onBackPressedCallback)
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.web_toolbar_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_web_back -> {
                if (view?.web_view1?.canGoBack() == true) view?.web_view1?.goBack()
            }
            R.id.action_web_forward -> {
                if (view?.web_view1?.canGoForward() == true) view?.web_view1?.goForward()
            }
            R.id.action_web_cancel -> {
                APP_CONTEXT?.navController?.navigate(R.id.action_webViewFragment_to_mainFragment)
            }
            R.id.action_web_openbrow -> {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(view?.web_view1?.url))
                startActivity(intent)
            }
        }



        return super.onOptionsItemSelected(item)
    }
}
