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
    override fun updateData(data: IContentResponse) {
        if ((data as? StdContentResponse?) != null) {
            tempSetupStdContent(data)
        }
    }


    private fun tempSetupStdContent(data: StdContentResponse) {
        Log.i("NYA_$TAG", "Updating std data.")

        val photos = data.response.photos
        if (photos != null) {

            val currentPage = photos.page
            if (currentPage != null) {
                results_current_page.text = getString(R.string.current_page, currentPage)
            } else {
                Log.i("NYA_$TAG", "StdContentResponse current page is null")
            }

            val lastPage = photos.pages
            if (lastPage != null) {
                results_last_page.text = getString(R.string.last_page, lastPage)
            } else {
                Log.i("NYA_$TAG", "StdContentResponse last page is null")
            }

            val photo = photos.photo?.map { photo -> photo.constructURL() }
            if (photo != null) {
                results_data_list.text = getString(R.string.data_list, photo)
            } else {
                Log.i("NYA_$TAG", "StdContentResponse Photo list is null")
            }

            results_query.text = getString(R.string.query_ui, data.query)

            if (currentPage != null && lastPage != null && photo != null) {
                (parentFragment as IPagerHost).pagerDataChanged(object : StdPagerData() {

                    override val query: String = data.query
                    override val currentPage: Int = currentPage
                    override val lastPage: Int = lastPage
                    override val pagerList: List<String> = photo

                })
            } else {
                Log.i("NYA_$TAG", "One of the important StdContentResponse elements is null. " +
                        "No data change notification.")
            }

        } else {
            Log.i("NYA_$TAG", "StdContentResponse Photos class is null")
        }
    }


}