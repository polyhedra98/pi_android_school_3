package com.mishenka.notbasic.map


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.mishenka.notbasic.R
import com.mishenka.notbasic.databinding.FragmentMapSearchBinding
import com.mishenka.notbasic.home.HomeAdapter
import com.mishenka.notbasic.home.HomeVM
import com.mishenka.notbasic.util.Constants.PER_PAGE
import com.mishenka.notbasic.util.obtainAuthVM
import com.mishenka.notbasic.util.obtainHomeVM

class MapSearchFragment : Fragment() {


    private lateinit var binding: FragmentMapSearchBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        (DataBindingUtil.inflate(inflater, R.layout.fragment_map_search, container, false)
                as FragmentMapSearchBinding)
            .apply {
                homeVM = (activity as AppCompatActivity).obtainHomeVM()
                lifecycleOwner = activity
            }.also { binding = it }.root


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupBindings()
    }


    private fun setupBindings() {
        with(binding) {
            homeVM?.apply {
                endlessPreferred.observe(this@MapSearchFragment, Observer {
                    setupRecyclerView(it)
                })
                mapSearchResultsList.observe(this@MapSearchFragment, Observer {
                    observeResults(it, mapSearchResultsRv, endlessPreferred.value!!)
                })
                mapNextPageTv.setOnClickListener {
                    changeMapPage(1)
                }
                mapPrevPageTv.setOnClickListener {
                    changeMapPage(-1)
                }
            }
        }
    }


    private fun observeResults(results: List<String>, rv: RecyclerView, isEndless: Boolean) {
        Log.i("NYA", "(from MapSearchFragment) Results: $results")
        with(rv) {
            val length = results.size
            if (!isEndless) {
                (adapter as RecyclerView.Adapter?)
                    ?.notifyDataSetChanged()
                scrollToPosition(0)
            } else {
                (adapter as RecyclerView.Adapter?)
                    ?.notifyItemRangeChanged(length - PER_PAGE + 1, length)
            }
        }
    }


    private fun setupRecyclerView(endlessPreferred: Boolean) {
        with(binding) {
            val layoutManager = LinearLayoutManager(context!!, RecyclerView.VERTICAL, false)

            mapSearchResultsRv.layoutManager = layoutManager
            val onScrollListener = OnBottomScrollListener(homeVM!!, layoutManager)
            if (endlessPreferred) {
                if (!homeVM!!.mapSearchResultsList.value.isNullOrEmpty()) {
                    homeVM!!.changePage(0)
                }
                mapSearchResultsRv.addOnScrollListener(onScrollListener)
                mapSearchResultsRv.adapter = MapAdapter(homeVM!!)
            } else {
                Log.i("NYA", "No endless")
                if (!homeVM!!.mapSearchResultsList.value.isNullOrEmpty()) {
                    homeVM!!.trimMapResultsList()
                }
                mapSearchResultsRv.removeOnScrollListener(onScrollListener)
                mapSearchResultsRv.adapter = MapAdapter(homeVM!!)
            }
        }
    }


    inner class OnBottomScrollListener(
        private val homeVM: HomeVM,
        private val layoutManager: LinearLayoutManager
    ) : RecyclerView.OnScrollListener() {

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            homeVM.mapSearchResultsList.value?.let { safeResultsList ->
                if (safeResultsList.isNotEmpty() && layoutManager
                        .findLastVisibleItemPosition() == safeResultsList.size) {
                    Log.i("NYA", "Reached the bottom")
                    homeVM.continuousMapSearch()
                }
            }
            super.onScrolled(recyclerView, dx, dy)
        }

    }


    companion object {

        fun newInstance() = MapSearchFragment()

    }

}
