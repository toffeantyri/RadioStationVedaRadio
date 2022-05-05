package ru.music.radiostationvedaradio.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import ru.music.radiostationvedaradio.viewmodel.ViewModelMainActivity

const val TAG_WEB_URL = "web_url"

class WebViewFragment : Fragment() {

    private val dataModel: ViewModelMainActivity by activityViewModels()
    lateinit var webUrl: String
    private var mActivity: MainActivity? = null
    private var drawerMenuParent: DrawerLayout? = null

    companion object {
        @JvmStatic
        fun newInstance(urlSite: String) = WebViewFragment().apply {
            val args = Bundle()
            args.putString(TAG_WEB_URL, urlSite)
            arguments = args
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view0 = inflater.inflate(R.layout.fragment_web_view, container, false)
        webUrl = arguments?.getString(TAG_WEB_URL) ?: ""
        mActivity = activity as MainActivity
        drawerMenuParent = mActivity?.myDrawerLayout


        view0.setOnClickToolbarButton()



        return view0
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onBackPressed(view.web_view1)
        dataModel.statusFragmentConnected.value = true

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
                if (webView.canGoBack()) {
                    webView.goBack()
                } else {
                    dataModel.statusFragmentConnected.value = false
                    activity?.supportFragmentManager?.beginTransaction()?.remove(this@WebViewFragment)?.commit()
                }
            }
        }
        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, onBackPressedCallback)
    }

    private fun View.setOnClickToolbarButton() {
        this.fr_toolb_menu.setOnClickListener {
            if (drawerMenuParent != null) {
                if (drawerMenuParent!!.isDrawerOpen(GravityCompat.START)) drawerMenuParent!!.closeDrawer(GravityCompat.START)
                else drawerMenuParent!!.openDrawer(GravityCompat.START)
            }
        }
        this.fr_toolb_back.setOnClickListener {
            if (web_view1.canGoBack()) web_view1.goBack()
        }
        this.fr_toolb_forward.setOnClickListener {
            if (web_view1.canGoForward()) web_view1.goForward()
        }
        this.fr_toolb_cancel.setOnClickListener {
            dataModel.statusFragmentConnected.value = false
            activity?.supportFragmentManager?.beginTransaction()?.remove(this@WebViewFragment)?.commit()
        }
        this.fr_toolb_browser.setOnClickListener {
            web_view1.url
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(web_view1.url))
            startActivity(intent)
        }
    }

}
