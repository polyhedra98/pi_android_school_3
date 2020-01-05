package com.mishenka.notbasic.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mishenka.notbasic.R
import com.mishenka.notbasic.data.content.StdContentResponse
import com.mishenka.notbasic.interfaces.IContentResponse
import com.mishenka.notbasic.interfaces.IPager
import com.mishenka.notbasic.interfaces.IPagerHost
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


    //TODO("Implementation will be changed once I add RecyclerView.")
    override fun updateData(data: IContentResponse) {
        if ((data as? StdContentResponse?) != null) {
            Log.i("NYA_$TAG", "Updating data.")
            results_data_list.text = getString(R.string.data_list, data.responseList.toString())
        }
    }


}