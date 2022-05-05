package ru.music.radiostationvedaradio.fragments

import android.graphics.Bitmap
import android.util.Log
import android.view.View
import android.webkit.*
import android.widget.Toast
import kotlinx.android.synthetic.main.fragment_web_view.view.*

class WebClientForFragment(private val rootView: View) : WebViewClient() {
    override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
        super.onReceivedError(view, request, error)
        Log.d("MyLog", "error loading webPage ${error?.description}")
        if(error?.description == "net::ERR_INTERNET_DISCONNECTED"){
            Toast.makeText(view?.context, "Internet Disconnected", Toast.LENGTH_SHORT ).show()
        }
    }

    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        super.onPageStarted(view, url, favicon)
        view?.visibility = View.VISIBLE
        rootView.progress_cicle_webpage.visibility = View.GONE
    }

}

class WebChromeClientForFragment(private val rootView: View) : WebChromeClient() {


    override fun onReceivedIcon(view: WebView?, icon: Bitmap?) {
        super.onReceivedIcon(view, icon)

    }
}