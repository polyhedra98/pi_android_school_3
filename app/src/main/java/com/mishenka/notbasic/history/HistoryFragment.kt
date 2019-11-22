package com.mishenka.notbasic.history


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
import com.mishenka.notbasic.databinding.FragmentHistoryBinding
import com.mishenka.notbasic.util.Event
import com.mishenka.notbasic.util.obtainAuthVM
import com.mishenka.notbasic.util.obtainHomeVM
import kotlinx.android.synthetic.main.fragment_history.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.lang.IllegalStateException

class HistoryFragment : Fragment() {

    private lateinit var binding: FragmentHistoryBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        (DataBindingUtil.inflate(inflater, R.layout.fragment_history, container, false)
                as FragmentHistoryBinding)
            .apply {
                homeVM = (activity as AppCompatActivity).obtainHomeVM().apply {
                    getUserHistory((activity as AppCompatActivity).obtainAuthVM().userId.value)
                }
                authVM = (activity as AppCompatActivity).obtainAuthVM()

                lifecycleOwner = activity
            }.also { binding = it }.root


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {

            historyRv.adapter = HistoryAdapter(homeVM!!)
            historyRv.layoutManager =
                LinearLayoutManager(context!!, RecyclerView.VERTICAL, false)

        }
    }

    companion object {

        fun newInstance() = HistoryFragment()

    }
}
