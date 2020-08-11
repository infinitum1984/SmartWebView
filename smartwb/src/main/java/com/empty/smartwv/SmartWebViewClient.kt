package com.empty.smartwv

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.webkit.*
import android.widget.FrameLayout

class SmartWebViewClient(val activity: Activity):  WebChromeClient() {

    private var mUploadMessage: ValueCallback<Uri?>? = null
    var uploadMessage: ValueCallback<Array<Uri>>? = null
    var mDecorView: View? = null
    private lateinit var webView: WebView
    private var customViewContainer: FrameLayout? = null
    private var customViewCallback: CustomViewCallback? = null
    private var mCustomView: View? = null
    val context:Context
    val REQUEST_SELECT_FILE = 100
    private val FILECHOOSER_RESULTCODE = 1
    init {
        context=activity.applicationContext

    }
    private var mVideoProgressView: View? = null
    override fun onShowCustomView(
        view: View,
        requestedOrientation: Int,
        callback: CustomViewCallback
    ) {
        onShowCustomView(
            view,
            callback
        ) //To change body of overridden methods use File | Settings | File Templates.
    }

    override fun onShowCustomView(
        view: View,
        callback: CustomViewCallback
    ) {

        // if a view already exists then immediately terminate the new one
        if (mCustomView != null) {
            callback.onCustomViewHidden()
            return
        }
        mCustomView = view
        view.visibility = View.GONE
        customViewContainer!!.visibility = View.VISIBLE
        customViewContainer!!.addView(view)
        customViewCallback = callback
        hideSystemUI()
    }

    override fun getVideoLoadingProgressView(): View? {
        if (mVideoProgressView == null) {
//            val inflater = LayoutInflater.from(context)
//            mVideoProgressView = inflater.inflate(R.layout.load_progress, null)
        }
        return mVideoProgressView
    }

    override fun onHideCustomView() {
        super.onHideCustomView()


        //To change body of overridden methods use File | Settings | File Templates.
        if (mCustomView == null) return
        customViewContainer!!.visibility = View.GONE

        // Hide the custom view.

        // Remove the custom view from its container
        //
        mCustomView!!.visibility = View.GONE
        customViewContainer!!.removeView(mCustomView)
        customViewCallback!!.onCustomViewHidden()
        mCustomView = null
        webView!!.visibility = View.VISIBLE
        showSystemUI()
    }


    // For Lollipop 5.0+ Devices
    override fun onShowFileChooser(
        mWebView: WebView,
        filePathCallback: ValueCallback<Array<Uri>>,
        fileChooserParams: FileChooserParams
    ): Boolean {
        if (uploadMessage != null) {
            uploadMessage!!.onReceiveValue(null)
            uploadMessage = null
        }
        uploadMessage = filePathCallback
        var intent: Intent? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            intent = fileChooserParams.createIntent()
        } else {
        }
        try {
            activity.startActivityForResult(intent,
                REQUEST_SELECT_FILE
            )
        } catch (e: ActivityNotFoundException) {
            uploadMessage = null
            return false
        }
        return true
    }

    //For Android 4.1 only
    protected fun openFileChooser(
        uploadMsg: ValueCallback<Uri?>?,
        acceptType: String?,
        capture: String?
    ) {
        showUploadDialog(uploadMsg)
        return
    }

    protected fun openFileChooser(uploadMsg: ValueCallback<Uri?>?) {
        showUploadDialog(uploadMsg)
        return
    }

    fun showUploadDialog(uploadMsg: ValueCallback<Uri?>?) {
        mUploadMessage = uploadMsg
        val i = Intent(Intent.ACTION_GET_CONTENT)
        i.addCategory(Intent.CATEGORY_OPENABLE)
        i.type = "*/*"
        activity.startActivityForResult(
            Intent.createChooser(i, "File Chooser"),
            FILECHOOSER_RESULTCODE
        )
    }

    override fun onCreateWindow(
        view: WebView?,
        isDialog: Boolean,
        isUserGesture: Boolean,
        resultMsg: Message?
    ): Boolean {
        val newWebView = WebView(context);
        newWebView.getSettings().setJavaScriptEnabled(true);
        newWebView.getSettings().setSupportZoom(true);
        newWebView.getSettings().setBuiltInZoomControls(true);
        newWebView.getSettings().setPluginState(WebSettings.PluginState.ON);
        newWebView.getSettings().setSupportMultipleWindows(true);
        view!!.addView(newWebView);
        val transport = resultMsg!!.obj as WebView.WebViewTransport
        transport.setWebView(newWebView);
        resultMsg.sendToTarget();

        newWebView.setWebViewClient(object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url:String): Boolean {
                view.loadUrl(url);
                return true;
            }
        });

        return true;
    }
    private fun hideSystemUI() {
        // Используем флаг IMMERSIVE.
        mDecorView!!.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // прячем панель навигации
                or View.SYSTEM_UI_FLAG_FULLSCREEN // прячем строку состояния
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
    }

    // Программно выводим системные панели обратно
    private fun showSystemUI() {
        mDecorView!!.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // прячем панель навигации
                or View.SYSTEM_UI_FLAG_FULLSCREEN // прячем строку состояния
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        var uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN
        mDecorView!!.systemUiVisibility = uiOptions
        uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        mDecorView!!.systemUiVisibility = uiOptions
    }


}