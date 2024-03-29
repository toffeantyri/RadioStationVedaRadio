package ru.music.radiostationvedaradio.ui.screens

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.webkit.*
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import ru.music.radiostationvedaradio.R
import ru.music.radiostationvedaradio.databinding.FragmentWebViewBinding
import ru.music.radiostationvedaradio.ui.MainActivity


const val TAG_WEB_URL = "web_url"

class WebViewFragment : Fragment() {

    private lateinit var binding: FragmentWebViewBinding
    private lateinit var webUrl: String
    private lateinit var parentActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWebViewBinding.inflate(inflater)
        webUrl = arguments?.getString(TAG_WEB_URL) ?: ""
        parentActivity = (activity as MainActivity)
        setHasOptionsMenu(true)
        return binding.root
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        overrideOnBackPressedWithCallback(binding.webView1)
        val myWebClient = object : WebViewClient() {
            override fun onReceivedError(
                view: WebView?,
                request: WebResourceRequest?,
                error: WebResourceError?
            ) {
                super.onReceivedError(view, request, error)
                Log.d("MyLog", "error loading webPage ${error?.description}")
                if (error?.description == "net::ERR_INTERNET_DISCONNECTED") {
                    Toast.makeText(view?.context, "Internet Disconnected", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                view?.visibility = View.VISIBLE
                parentActivity.webUrl = url
                binding.progressCicleWebpage.visibility = View.GONE
            }


        }

        view.apply {

            binding.progressCicleWebpage.visibility = View.VISIBLE
            binding.webView1.apply {
                webViewClient = myWebClient
                webChromeClient = WebChromeClient()
                settings.apply {
                    setSupportZoom(true)

                    javaScriptEnabled = true
                    allowFileAccess = false

                    builtInZoomControls = true
                    domStorageEnabled = true
                    loadWithOverviewMode = true
                    useWideViewPort = true
                    defaultTextEncodingName = "utf-8"

                    loadsImagesAutomatically = true
                    mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                }
                setBackgroundColor(resources.getColor(R.color.green_100_alp50))
                loadUrl(webUrl)
            }
        }
    }


    private fun overrideOnBackPressedWithCallback(webView: WebView) {
        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                Log.d("MyLog", "handleOnBackpressed")
                if (webView.canGoBack()) {
                    webView.goBack()
                } else {
                    val action = WebViewFragmentDirections.actionWebViewFragmentToMainFragment()
                    findNavController().navigate(action)
                }
            }
        }
        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, onBackPressedCallback)
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.web_toolbar_menu, menu)
        menu.findItem(R.id.action_refresh).isVisible = false
        menu.findItem(R.id.action_play).isVisible = false
        menu.findItem(R.id.action_low_quality).isVisible = false
        menu.findItem(R.id.action_medium_quality).isVisible = false
        menu.findItem(R.id.action_high_quality).isVisible = false
    }

    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        with(binding) {
            when (item.itemId) {
                R.id.action_web_back -> {
                    if (webView1.canGoBack()) webView1.goBack()
                }

                R.id.action_web_forward -> {
                    if (webView1.canGoForward()) webView1.goForward()
                }

                R.id.action_web_cancel -> {
                    val action = WebViewFragmentDirections.actionWebViewFragmentToMainFragment()
                    findNavController().navigate(action)
                }

                R.id.action_web_openbrow -> {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(webView1.url))
                    startActivity(intent)
                }
            }
            return super.onOptionsItemSelected(item)
        }
    }


}
