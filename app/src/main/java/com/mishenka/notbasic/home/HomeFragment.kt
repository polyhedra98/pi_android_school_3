package com.mishenka.notbasic.home

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.mishenka.notbasic.R
import com.mishenka.notbasic.databinding.FragmentHomeBinding
import com.mishenka.notbasic.util.obtainHomeVM
import kotlinx.android.synthetic.main.fragment_home.*


class HomeFragment private constructor() : Fragment() {


    private lateinit var viewModel: HomeVM


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        (DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
                as FragmentHomeBinding)
            .apply {
                homeVM = (activity as AppCompatActivity).obtainHomeVM().apply {
                    start(context!!)
                }.also {
                    viewModel = it
                }
                lifecycleOwner = activity
            }.root


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.apply {
            search_b.setOnClickListener {
                search()
            }
            search_results_tv.movementMethod = ScrollingMovementMethod()
            next_page_tv.setOnClickListener {
                changePage(1)
            }
            prev_page_tv.setOnClickListener {
                changePage(-1)
            }
        }
    }

    companion object {

        fun newInstance() = HomeFragment()

    }

}
