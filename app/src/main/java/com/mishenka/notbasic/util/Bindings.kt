package com.mishenka.notbasic.util

import android.text.util.Linkify
import android.widget.EditText
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.lifecycle.LiveData

object Bindings {

    @BindingAdapter("app:observable_error")
    @JvmStatic fun observeError(et: EditText, observable_error: LiveData<String?>) {
        if (observable_error.value.isNullOrEmpty()) {
            et.error = null
            et.clearFocus()
            et.text.clear()
        } else {
            et.error = observable_error.value
            et.selectAll()
        }
    }

    @BindingAdapter("app:linkified_results")
    @JvmStatic fun linkifyResults(tv: TextView, results: String) {
        tv.text = results
        Linkify.addLinks(tv, Linkify.WEB_URLS)
    }

}