package com.mishenka.notbasic.history

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mishenka.notbasic.R
import com.mishenka.notbasic.home.HomeVM

class HistoryAdapter(
    private val homeVM: HomeVM
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        HistoryHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.item_history, parent, false))


    override fun getItemCount() =
        homeVM.historyList.value!!.size


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        with(holder as HistoryHolder) {
            searchTV?.let { safeSearchTV ->
                safeSearchTV.text = homeVM.historyList.value!![position].historyItem
            }
            timeTV?.let { safeTimeTV ->
                safeTimeTV.text = homeVM.historyList.value!![position].timeStamp.toString()
            }
        }
    }

    class HistoryHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val searchTV: TextView? = itemView.findViewById(R.id.history_search_tv)
        val timeTV: TextView? = itemView.findViewById(R.id.history_time_tv)
    }
}