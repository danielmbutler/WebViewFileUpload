package com.dbtechprojects.testfileupload

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ClipData
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.webkit.*

var uploadMessage: ValueCallback<Array<Uri>>? = null
private const val file_type = "*/*" // file types to be allowed for upload

private const val multiple_files = true


class MainActivity : Activity() {
    @SuppressLint("JavascriptInterface")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)


            val myWebView: WebView = findViewById(R.id.webview01)
            myWebView.loadUrl("https://filebin.net")

            myWebView.webViewClient = WebViewClient()

            val webSettings = myWebView.settings
            webSettings.javaScriptEnabled = true
            webSettings.setAllowFileAccess(true);
            webSettings.setDomStorageEnabled(true);
            webSettings.setAllowContentAccess(true);
            webSettings.setAllowFileAccess(true);
            webSettings.setJavaScriptCanOpenWindowsAutomatically(true);

            myWebView.setWebViewClient(object : WebViewClient() {

                override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {

                    val i = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    startActivity(i)

                    return true

                }


            })



            myWebView.setWebChromeClient(object : WebChromeClient() {


                override fun onShowFileChooser(
                        view: WebView,
                        filePathCallback: ValueCallback<Array<Uri>>,
                        fileChooserParams: FileChooserParams
                ): Boolean {

                    uploadMessage = filePathCallback


                    val contentSelectionIntent = Intent(Intent.ACTION_GET_CONTENT)
                    contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE)
                    contentSelectionIntent.type = file_type
                    if (multiple_files) {
                        contentSelectionIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                    }

                    val chooserIntent = Intent(Intent.ACTION_CHOOSER)
                    chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent)
                    chooserIntent.putExtra(Intent.EXTRA_TITLE, "File chooser")
                    startActivityForResult(chooserIntent, 1)
                    return true


                }

    })
    }




    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        if (Build.VERSION.SDK_INT >= 21) {
            var results: Array<Uri?>? = null

            if (resultCode == RESULT_CANCELED) {
                uploadMessage?.onReceiveValue(null)
                return
            }

            Log.d("danieldebug", "override called")

            if (resultCode == RESULT_OK) {

                var clipData: ClipData?
                var stringData: String?
                try {
                    clipData = intent!!.clipData
                    stringData = intent.dataString
                    Log.d("debug", "clipdata: $clipData")
                    Log.d("debug", "stringdata: $stringData")
                } catch (e: Exception) {
                    clipData = null
                    stringData = null
                }

                if (clipData != null) { // checking if multiple files selected or not
                    val numSelectedFiles = clipData.itemCount
                    results = arrayOfNulls(numSelectedFiles)
                    for (i in 0 until clipData.itemCount) {
                        results[i] = clipData.getItemAt(i).uri
                    }
                }
            }
            if (results != null){
                Log.d("debug", "results are not null")
                Log.d("debug", "$results")
                uploadMessage?.onReceiveValue(results as Array<Uri>)
            }

            uploadMessage = null

        }
    }
}
