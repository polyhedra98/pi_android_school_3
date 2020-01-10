package com.mishenka.notbasic.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mishenka.notbasic.R
import com.mishenka.notbasic.interfaces.*
import com.mishenka.notbasic.utils.recycler.*
import kotlinx.android.synthetic.main.fragment_results.*


class ResultsFragment : Fragment(), IPager {

    val TAG = "ResultsFragment"


    var data: IPagerData? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_results, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (parentFragment as IPagerHost).pagerSetupRequested()

        setupPagerButtons()

        super.onViewCreated(view, savedInstanceState)
    }


    override fun updateData(data: IPagerData) {
        this.data = data

        results_rv.scrollToPosition(0)
        (results_rv.adapter as ResponsiveHeaderlessAdapter<PhotosViewHolder>?)
            ?.replaceItems(data.pagerList)
        updatePagerButtons()
    }


    override fun updateHeader(newHeader: PagerElement) {
        (results_rv.adapter as? ResponsiveAdapter<PhotosViewHolder>?)
            ?.replaceHeader(newHeader)
    }


    override fun setupRecycler(adapter: ResponsiveHeaderlessAdapter<PhotosViewHolder>) {
        Log.i("NYA_$TAG", "Setting up recycler.")
        results_rv.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)

        ItemTouchHelper(SwipeItemTouchHelperCallback(object : ISwipeListener {
            override fun onItemDismiss(position: Int) {
                (results_rv.adapter as ResponsiveHeaderlessAdapter<PhotosViewHolder>?)
                    ?.removeItem(position)
            }
        })).attachToRecyclerView(results_rv)

        results_rv.adapter = adapter
    }


    private fun setupPagerButtons() {
        results_previous_page_b.setOnClickListener {
            (parentFragment as IPagerHost).pageChangeRequested(data!!.currentPage - 1)
        }
        results_next_page_b.setOnClickListener {
            (parentFragment as IPagerHost).pageChangeRequested(data!!.currentPage + 1)
        }
    }


    private fun updatePagerButtons() {
        if (data!!.currentPage > 1) {
            results_previous_page_b.visibility = View.VISIBLE
        } else {
            results_previous_page_b.visibility = View.INVISIBLE
        }

        if (data!!.currentPage < data!!.lastPage) {
            results_next_page_b.visibility = View.VISIBLE
        } else {
            results_next_page_b.visibility = View.INVISIBLE
        }
    }


}