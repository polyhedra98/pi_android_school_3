package com.mishenka.notbasic.utils.recycler

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mishenka.notbasic.R
import com.mishenka.notbasic.data.model.user.HistorySelectItem


class HistoryAdapter(
    private val items: List<HistorySelectItem?>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        HistoryHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_history, parent, false))


    override fun getItemCount() = items.size


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        with(holder as HistoryHolder) {
            searchTV?.let { safeSearchTV ->
                safeSearchTV.text = items[position]?.search
            }
            pageTV?.let { safePageTV ->
                safePageTV.text = safePageTV.context.getString(
                    R.string.history_page_ui,
                    items[position]?.page
                )
            }
            timeTV?.let { safeTimeTV ->
                safeTimeTV.text = items[position]?.timeStamp?.toString()
            }
        }
    }

    class HistoryHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val searchTV: TextView? = itemView.findViewById(R.id.item_history_search_tv)
        val pageTV: TextView? = itemView.findViewById(R.id.item_history_page_tv)
        val timeTV: TextView? = itemView.findViewById(R.id.item_history_timestamp_tv)
    }
}