package com.mishenka.notbasic.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mishenka.notbasic.R
import com.mishenka.notbasic.data.pager.LatLngPagerData
import com.mishenka.notbasic.data.pager.StdPagerData
import com.mishenka.notbasic.interfaces.*
import com.mishenka.notbasic.utils.recycler.HomeAdapter
import com.mishenka.notbasic.utils.recycler.PhotosAdapter
import com.mishenka.notbasic.utils.recycler.PhotosViewHolder
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
        (parentFragment as IPagerHost).requestSetup()

        setupPagerButtons()

        super.onViewCreated(view, savedInstanceState)
    }


    override fun updateData(data: IPagerData) {
        this.data = data

        (results_rv.adapter as PhotosAdapter<PhotosViewHolder, PhotosViewHolder>)
            .replaceItems(data.pagerList)
        updatePagerButtons()
    }


    override fun setupRecycler(adapter: PhotosAdapter<PhotosViewHolder, PhotosViewHolder>) {
        Log.i("NYA_$TAG", "Setting up recycler.")
        results_rv.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
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