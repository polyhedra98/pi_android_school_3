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
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.mishenka.notbasic.R
import com.mishenka.notbasic.databinding.FragmentMapSearchBinding
import com.mishenka.notbasic.home.HomeVM
import com.mishenka.notbasic.util.Constants.PER_PAGE
import com.mishenka.notbasic.util.SwipeItemTouchHelperCallback
import com.mishenka.notbasic.util.SwipeListener
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
                    observeResults(it, mapSearchResultsRv)
                })
                mapResultsField.observe(this@MapSearchFragment, Observer {
                    observeHeader(it, mapSearchResultsRv)
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


    private fun observeHeader(header: String, rv: RecyclerView) {
        (rv.adapter as MapAdapter?)?.replaceHeader(header)
    }


    private fun observeResults(results: List<String>, rv: RecyclerView) {
        val homeVM = (activity as AppCompatActivity).obtainHomeVM()
        with(rv) {
            if (!homeVM.endlessPreferred.value!!) {
                (adapter as MapAdapter?)?.replaceItems(results)
                scrollToPosition(0)
            } else {
                (adapter as MapAdapter?)?.pseudoAddItems(results)
            }
        }
    }


    private fun setupRecyclerView(endlessPreferred: Boolean) {
        with(binding) {
            val layoutManager = LinearLayoutManager(context!!, RecyclerView.VERTICAL, false)

            mapSearchResultsRv.layoutManager = layoutManager
            val onScrollListener = OnBottomScrollListener(homeVM!!, layoutManager)
            ItemTouchHelper(SwipeItemTouchHelperCallback(MapSwipeListener()))
                .attachToRecyclerView(mapSearchResultsRv)

            if (endlessPreferred) {
                val items = if (!homeVM!!.mapSearchResultsList.value.isNullOrEmpty()) {
                    homeVM!!.changeMapPage(0)
                    homeVM!!.mapSearchResultsList.value!!
                } else {
                    listOf(homeVM!!.resultsField.value!!)
                }
                mapSearchResultsRv.addOnScrollListener(onScrollListener)
                mapSearchResultsRv.adapter = MapAdapter(items, homeVM!!)
            } else {
                val items = if (!homeVM!!.mapSearchResultsList.value.isNullOrEmpty()) {
                    homeVM!!.trimMapResultsList()
                    homeVM!!.mapSearchResultsList.value!!
                } else {
                    listOf(homeVM!!.mapResultsField.value!!)
                }
                mapSearchResultsRv.removeOnScrollListener(onScrollListener)
                mapSearchResultsRv.adapter = MapAdapter(items, homeVM!!)
            }
        }
    }


    class OnBottomScrollListener(
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


    inner class MapSwipeListener: SwipeListener {

        override fun onItemDismiss(position: Int) {
            (binding.mapSearchResultsRv.adapter as MapAdapter?)?.removeItem(position)
        }

    }


    companion object {

        fun newInstance() = MapSearchFragment()

    }

}
