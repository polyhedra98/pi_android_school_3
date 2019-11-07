package com.mishenka.notbasic.detail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView
import android.widget.TextView
import com.mishenka.notbasic.R

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intentData = intent.data.toString()
        if (intentData.contains("https://")) {
            val webView = WebView(this)
            setContentView(webView)
            webView.loadUrl(intentData)
        } else {
            val textView = TextView(this)
            setContentView(textView)
            textView.text = getString(R.string.url_error)
        }
    }
}
