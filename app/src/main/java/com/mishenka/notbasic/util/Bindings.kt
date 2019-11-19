package com.mishenka.notbasic.util

import android.text.util.Linkify
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.databinding.BindingAdapter

object Bindings {

    @BindingAdapter("observable_error")
    @JvmStatic fun observeError(et: EditText, observable_error: String?) {
        if (observable_error.isNullOrEmpty()) {
            et.error = null
            et.clearFocus()
        } else {
            et.error = observable_error
            et.selectAll()
        }
    }

    @BindingAdapter("linkified_results")
    @JvmStatic fun linkifyResults(tv: TextView, results: String) {
        tv.text = results
        Linkify.addLinks(tv, Linkify.WEB_URLS)
    }

    @BindingAdapter("bind_prev")
    @JvmStatic fun bindPrevPage(tv: TextView, currentPage: Int?) {
        if (currentPage == null || currentPage < 2) {
            tv.visibility = View.INVISIBLE
        } else {
            tv.visibility = View.VISIBLE
        }
    }

    @BindingAdapter(value = ["bind_next_current", "bind_next_last"])
    @JvmStatic fun bindNextPage(tv: TextView, currentPage: Int?, lastPage: Int?) {
        if (currentPage == null || lastPage == null || currentPage == lastPage) {
            tv.visibility = View.INVISIBLE
        } else {
            tv.visibility = View.VISIBLE
        }
    }

    @BindingAdapter("loading_progress")
    @JvmStatic fun loadingProgress(pb: ProgressBar, loading: Boolean?) {
        if (loading == null || !loading) {
            pb.visibility = View.GONE
        } else {
            pb.visibility = View.VISIBLE
        }
    }

    @BindingAdapter("loading_results")
    @JvmStatic fun loadingResults(v: View, loading: Boolean?) {
        if (loading == null || !loading) {
            v.visibility = View.VISIBLE
        } else {
            v.visibility = View.GONE
        }
    }

}