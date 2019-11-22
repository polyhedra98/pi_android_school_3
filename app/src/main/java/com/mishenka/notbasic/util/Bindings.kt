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

    @BindingAdapter("observable_home_results")
    @JvmStatic fun observeResults(rv: RecyclerView, observable_results: List<String>) {
        with(rv) {
            (adapter as RecyclerView.Adapter?)?.notifyItemRangeChanged(1, observable_results.size)
            scrollToPosition(0)
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

    @BindingAdapter("observe_anon_user")
    @JvmStatic fun observeUserStateForAnonymous(view: View, observable_user: String?) {
        view.visibility = if (observable_user == null) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    @BindingAdapter("observe_user")
    @JvmStatic fun observeUserState(view: View, observable_user: String?) {
        view.visibility = if (observable_user == null) {
            View.GONE
        } else {
            View.VISIBLE
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