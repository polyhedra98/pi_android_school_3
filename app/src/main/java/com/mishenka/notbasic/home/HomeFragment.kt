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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mishenka.notbasic.R
import com.mishenka.notbasic.databinding.FragmentHomeBinding
import com.mishenka.notbasic.util.obtainAuthVM
import com.mishenka.notbasic.util.obtainHomeVM


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

        setupBindings()
    }


    private fun setupBindings() {
        with(binding) {
            homeVM?.apply {
                searchB.setOnClickListener {
                    search((activity as AppCompatActivity).obtainAuthVM().userId.value)
                }
                endlessPreferred.observe(this@HomeFragment, Observer {
                    setupRecyclerView(it)
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


    private fun setupRecyclerView(endlessPreferred: Boolean) {
        with(binding) {
            val layoutManager = LinearLayoutManager(context!!, RecyclerView.VERTICAL, false)

            searchResultsRv.layoutManager = layoutManager
            val onScrollListener = OnBottomScrollListener(homeVM!!, layoutManager)
            if (endlessPreferred) {
                searchResultsRv.adapter = HomeAdapter(homeVM!!)
                searchResultsRv.addOnScrollListener(onScrollListener)
            } else {
                Log.i("NYA", "No endless")
                searchResultsRv.removeOnScrollListener(onScrollListener)
                searchResultsRv.adapter = HomeAdapter(homeVM!!)
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


    companion object {

        fun newInstance() = HomeFragment()

    }

}
