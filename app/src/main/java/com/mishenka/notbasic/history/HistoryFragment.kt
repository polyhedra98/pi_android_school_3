package com.mishenka.notbasic.history


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.mishenka.notbasic.R
import com.mishenka.notbasic.databinding.FragmentHistoryBinding
import com.mishenka.notbasic.util.obtainAuthVM
import com.mishenka.notbasic.util.obtainHomeVM
import kotlinx.android.synthetic.main.fragment_history.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

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
                lifecycleOwner = activity
            }.also { binding = it }.root


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val userId = (activity as AppCompatActivity).obtainAuthVM().userId.value
        with(binding) {
            if (userId == null) {
                historyRv.visibility = View.GONE
                historyAuthErrorTv.text = getString(R.string.history_auth_error)
                historyAuthErrorTv.visibility = View.VISIBLE
            } else {
                historyAuthErrorTv.visibility = View.GONE
                val homeVM = (activity as AppCompatActivity).obtainHomeVM()
                //TODO("Change scope")
                GlobalScope.launch {
                    homeVM.getUserHistory(userId)
                    MainScope().launch {
                        historyRv.adapter = HistoryAdapter(homeVM)
                        historyRv.layoutManager =
                            LinearLayoutManager(context!!, RecyclerView.VERTICAL, false)
                        historyRv.visibility = View.VISIBLE
                    }
                }
            }
        }
    }


    companion object {

        fun newInstance() = HistoryFragment()

    }
}
