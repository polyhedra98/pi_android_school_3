package com.mishenka.notbasic.history


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity

import com.mishenka.notbasic.R
import com.mishenka.notbasic.util.obtainAuthVM
import com.mishenka.notbasic.util.obtainHomeVM
import kotlinx.android.synthetic.main.fragment_history.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class HistoryFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_history, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //TODO("Change scope")
        GlobalScope.launch {
            val textToSet = (activity as AppCompatActivity).obtainHomeVM()
                .getUserHistory((activity as AppCompatActivity).obtainAuthVM().userId.value)?.toString()
                ?: getString(R.string.empty_history)
            MainScope().launch {
                history_temp_tv.text = textToSet
            }
        }
    }


    companion object {

        fun newInstance() = HistoryFragment()

    }
}
