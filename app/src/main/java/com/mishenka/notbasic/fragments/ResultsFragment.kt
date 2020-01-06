package com.mishenka.notbasic.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mishenka.notbasic.R
import com.mishenka.notbasic.data.content.StdContentResponse
import com.mishenka.notbasic.fragments.data.StdPagerData
import com.mishenka.notbasic.interfaces.*
import kotlinx.android.synthetic.main.fragment_results.*


class ResultsFragment : Fragment(), IPager {

    val TAG = "ResultsFragment"


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_results, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (parentFragment as IPagerHost).requestSetup()

        super.onViewCreated(view, savedInstanceState)
    }


    //TODO("Temp implementation.")
    override fun updateData(data: IPagerData) {
        if ((data as? StdPagerData?) != null) {
            tempSetupStdContent(data)
        }
    }


    private fun tempSetupStdContent(data: StdPagerData) {
        Log.i("NYA_$TAG", "Updating std data.")

        results_query.text = getString(R.string.query_ui, data.query)
        results_current_page.text = getString(R.string.current_page, data.currentPage)
        results_last_page.text = getString(R.string.last_page, data.lastPage)
        results_data_list.text = getString(R.string.data_list, data.pagerList)

    }


}