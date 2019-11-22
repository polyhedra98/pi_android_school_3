package com.mishenka.notbasic.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
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
                homeVM = (activity as AppCompatActivity).obtainHomeVM().apply {
                    if (savedInstanceState == null) {
                        start(context!!)
                    }
                }
                lifecycleOwner = activity
            }.also { binding = it }.root


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            homeVM?.apply {
                searchB.setOnClickListener {
                    search((activity as AppCompatActivity).obtainAuthVM().userId.value)
                }
                searchResultsRv.adapter = HomeAdapter(this)
                searchResultsRv.layoutManager =
                    LinearLayoutManager(context!!, RecyclerView.VERTICAL, false)
                nextPageTv.setOnClickListener {
                    changePage(1)
                }
                prevPageTv.setOnClickListener {
                    changePage(-1)
                }
            }
        }
    }


    companion object {

        fun newInstance() = HomeFragment()

    }

}
