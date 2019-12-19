package com.mishenka.notbasic.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mishenka.notbasic.R
import com.mishenka.notbasic.databinding.FragmentHomeBinding
import com.mishenka.notbasic.util.*


class HomeFragment : Fragment() {


    private lateinit var binding: FragmentHomeBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        (DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
                as FragmentHomeBinding)
            .apply {
                homeVM = (activity as AppCompatActivity).obtainHomeVM()
                lifecycleOwner = activity
            }.also { binding = it }.root


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar(R.string.home_nav_title)

        setupBindings()
    }


    private fun setupBindings() {
        with(binding) {
            homeVM?.apply {
                searchB.setOnClickListener {
                    search(context!!, (activity as AppCompatActivity).obtainAuthVM().userId.value)
                }
                endlessPreferred.observe(this@HomeFragment, Observer {
                    setupRecyclerView(it)
                })
                resultsList.observe(this@HomeFragment, Observer {
                    observeResults(it, searchResultsRv)
                })
                resultsField.observe(this@HomeFragment, Observer {
                    observeHeader(it, searchResultsRv)
                })
                nextPageTv.setOnClickListener {
                    changePage(1)
                }
                prevPageTv.setOnClickListener {
                    changePage(-1)
                }
            }
        }
    }


    private fun observeHeader(header: String, rv: RecyclerView) {
        (rv.adapter as HomeAdapter?)?.replaceHeader(header)
    }


    private fun observeResults(results: List<String>, rv: RecyclerView) {
        val homeVM = (activity as AppCompatActivity).obtainHomeVM()
        Log.i("NYA", "Observing results in Home Fragment. Results: $results")
        with(rv) {
            if (!homeVM.endlessPreferred.value!!) {
                (adapter as HomeAdapter?)?.replaceItems(results)
                scrollToPosition(0)
            } else {
                (adapter as HomeAdapter?)?.pseudoAddItems(results)
            }
        }
    }


    private fun setupRecyclerView(endlessPreferred: Boolean) {
        with(binding) {
            val layoutManager = LinearLayoutManager(context!!, RecyclerView.VERTICAL, false)

            searchResultsRv.layoutManager = layoutManager
            val onScrollListener = OnBottomScrollListener(homeVM!!, layoutManager)
            ItemTouchHelper(SwipeItemTouchHelperCallback(HomeSwipeListener()))
                .attachToRecyclerView(searchResultsRv)

            if (endlessPreferred) {
                val items = if (!homeVM!!.resultsList.value.isNullOrEmpty()) {
                    homeVM!!.changePage(0)
                    homeVM!!.resultsList.value!!
                } else {
                    listOf(homeVM!!.resultsField.value!!)
                }
                searchResultsRv.addOnScrollListener(onScrollListener)
                searchResultsRv.adapter = HomeAdapter(items, homeVM!!)
            } else {
                val items = if (!homeVM!!.resultsList.value.isNullOrEmpty()) {
                    homeVM!!.trimResultsList()
                    homeVM!!.resultsList.value!!
                } else {
                    listOf(homeVM!!.resultsField.value!!)
                }
                searchResultsRv.removeOnScrollListener(onScrollListener)
                searchResultsRv.adapter = HomeAdapter(items, homeVM!!)
            }
        }
    }


    class OnBottomScrollListener(
        private val homeVM: HomeVM,
        private val layoutManager: LinearLayoutManager): RecyclerView.OnScrollListener() {

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            homeVM.resultsList.value?.let { safeResultsList ->
                if (safeResultsList.isNotEmpty() && layoutManager
                        .findLastVisibleItemPosition() == safeResultsList.size) {
                    Log.i("NYA", "Reached the bottom")
                    homeVM.continuousSearch()
                }
            }
            super.onScrolled(recyclerView, dx, dy)
        }

    }


    inner class HomeSwipeListener: SwipeListener {

        override fun onItemDismiss(position: Int) {
            Log.i("NYA", "Item $position dismiss")
            (binding.searchResultsRv.adapter as HomeAdapter?)?.removeItem(position)
        }

    }


    companion object {

        fun newInstance() = HomeFragment()

    }

}
