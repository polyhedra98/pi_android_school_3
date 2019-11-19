package com.mishenka.notbasic.detail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.util.Linkify
import android.webkit.WebView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.mishenka.notbasic.R
import kotlinx.android.synthetic.main.activity_detail.*

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        var url: String? = null
        intent?.extras?.let { safeExtras ->
            url = safeExtras.getString(getString(R.string.intent_url_extra))
        }
        url?.let { safeUrl ->
            Glide.with(detail_photo_iv.context)
                .load(safeUrl)
                .into(detail_photo_iv)

            detail_url_tv.text = safeUrl
            Linkify.addLinks(detail_url_tv, Linkify.WEB_URLS)
        }
    }

}
