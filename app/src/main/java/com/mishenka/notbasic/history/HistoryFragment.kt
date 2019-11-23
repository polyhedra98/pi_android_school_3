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

        setupBindings()
    }


    private fun setupBindings() {
        with(binding) {
            authVM?.userId?.observe(this@HistoryFragment, Observer { userId ->
                if (userId != null) {
                    homeVM?.let { safeHomeVM ->
                        historyAuthErrorTv.text = getString(R.string.fetching_history)
                        historyAuthErrorTv.visibility = View.VISIBLE
                        safeHomeVM.getUserHistory(userId) {
                            if (safeHomeVM.historyList.value!!.isEmpty()) {
                                historyAuthErrorTv.text = getString(R.string.empty_history)
                                historyRv.visibility = View.GONE
                                historyAuthErrorTv.visibility = View.VISIBLE
                            } else {
                                if (historyRv.adapter == null) {
                                    historyRv.adapter = HistoryAdapter(safeHomeVM)
                                    historyRv.layoutManager =
                                        LinearLayoutManager(context!!, RecyclerView.VERTICAL, false)
                                }
                                historyAuthErrorTv.visibility = View.GONE
                                historyRv.visibility = View.VISIBLE
                            }
                        }
                    }
                } else {
                    historyAuthErrorTv.text = getString(R.string.history_auth_error)
                    historyRv.visibility = View.GONE
                    historyAuthErrorTv.visibility = View.VISIBLE
                }
            })
        }
    }

    companion object {

        fun newInstance() = HistoryFragment()

    }
}
