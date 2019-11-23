package com.mishenka.notbasic.util

import android.text.util.Linkify
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mishenka.notbasic.data.model.user.FavouriteToShow
import com.mishenka.notbasic.data.model.user.HistorySelectItem
import com.mishenka.notbasic.favourites.FavouriteAdapter
import com.mishenka.notbasic.home.HomeVM

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

    @BindingAdapter("observe_favourites")
    @JvmStatic fun observeFavourites(rv: RecyclerView, observable_favourites: List<FavouriteToShow>) {
        with(rv) {
            (adapter as RecyclerView.Adapter?)?.notifyDataSetChanged()
            scrollToPosition(0)
        }
    }

    @BindingAdapter("observe_history")
    @JvmStatic fun observeHistory(rv: RecyclerView, observable_history: List<HistorySelectItem>) {
        with (rv) {
            (adapter as RecyclerView.Adapter?)?.notifyDataSetChanged()
            scrollToPosition(0)
        }
    }

    @BindingAdapter("observable_home_summary")
    @JvmStatic fun observeSummary(rv: RecyclerView, observable_summary: String?) {
        with(rv) {
            (adapter as RecyclerView.Adapter?)?.notifyItemChanged(0)
        }
    }

    @BindingAdapter("linkified_results")
    @JvmStatic fun linkifyResults(tv: TextView, results: String) {
        tv.text = results
        Linkify.addLinks(tv, Linkify.WEB_URLS)
    }

    @BindingAdapter(value = ["bind_prev", "endless"], requireAll = true)
    @JvmStatic fun bindPrevPage(tv: TextView, currentPage: Int?, endless: Boolean) {
        if (currentPage == null || currentPage < 2 || endless) {
            tv.visibility = View.INVISIBLE
        } else {
            tv.visibility = View.VISIBLE
        }
    }

    @BindingAdapter(value = ["bind_next_current", "bind_next_last", "endless"], requireAll = true)
    @JvmStatic fun bindNextPage(tv: TextView, currentPage: Int?, lastPage: Int?, endless: Boolean) {
        if (currentPage == null || lastPage == null || currentPage == lastPage || endless) {
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