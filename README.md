# WebViewFileUpload
Basic Acitivty to show how to upload single and multiple files to a webview in Kotlin

Repurposed code from https://github.com/mgks/Os-FileUp/ to kotlin and removed the camera and video options, this now a bare bones file uploader that allows multiple files.

```kotlin

override fun onShowFileChooser(webView: WebView, filePathCallback: ValueCallback<Array<Uri>>, fileChooserParams: FileChooserParams): Boolean {

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


Replaced OnActivityResult below

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        if (Build.VERSION.SDK_INT >= 21) {
            var results: Array<Uri?>? = null

            if (resultCode == RESULT_CANCELED) {
                uploadMessage?.onReceiveValue(null)
                return
            }

           if (resultCode == RESULT_OK) {

                var clipData: ClipData?
                var stringData: String?
                try {
                    clipData = intent!!.clipData
                    stringData = intent.dataString
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
                
                uploadMessage?.onReceiveValue(results as Array<Uri>)
            }

            uploadMessage = null

        }
    }
}
```

<p align="center">
  <img src="https://github.com/danielmbutler/WebViewFileUpload/blob/master/assets/Capture.PNG" width="300" >
</p>
