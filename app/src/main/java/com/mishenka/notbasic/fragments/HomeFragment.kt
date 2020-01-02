package com.mishenka.notbasic.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mishenka.notbasic.R
import com.mishenka.notbasic.data.DataTypes
import com.mishenka.notbasic.data.models.StdSearchExtras
import com.mishenka.notbasic.data.models.StdSearchResponse
import com.mishenka.notbasic.interfaces.*
import com.mishenka.notbasic.viewmodels.EventVM
import kotlinx.android.synthetic.main.fragment_home.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


class HomeFragment : Fragment() {

    private val TAG = "HomeFragment"


    private val eventVM by sharedViewModel<EventVM>()

    private var fragmentId: Long? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        arguments?.run {
            fragmentId = getLong(getString(R.string.bundle_fragment_id_key))
        }
        return inflater.inflate(R.layout.fragment_home, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSearchButton()

        setupRecyclerView()
    }


    private fun setupSearchButton() {
        search_b.setOnClickListener {
            if (fragmentId != null) {
                eventVM.requestData(object : IRequestData {

                    override val extras = StdSearchExtras(
                        page = 1,
                        searchQuery = search_et.text?.toString()
                    )

                    override val ofType = DataTypes.STD_SEARCH

                    override val fragmentId = this@HomeFragment.fragmentId!!

                    override val callback = object : IResponseCallback {

                        override fun onSuccess(data: IResponseData) {
                            //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                            Log.i("NYA_$TAG", "Successfully fetched data.")
                            val searchData = (data as StdSearchResponse)
                            Log.i("NYA_$TAG", "List: ${searchData.data?.photos?.photo}")
                        }

                        override fun onDataNotAvailable(msg: String) {
                            //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                            Log.i("NYA_$TAG", "Error. Failed to fetch data. $msg")
                        }

                    }

                })
            } else {
                Log.i("NYA_${HomeFragmentRequest.fragmentTag}", "Fragment ID is " +
                        "null. Can't request search data.")
            }
        }
    }


    private fun setupRecyclerView() {

    }


    object HomeFragmentRequest : IFragmentRequest {

        override val fragmentTag: String
            get() = "HOME_FRAG"

        override val navigationTitleId: Int
            get() = R.string.nav_home_title

        override val shouldBeDisplayedAlone: Boolean
            get() = false

        override val isSecondary: Boolean
            get() = false

        override val shouldHideToolbar: Boolean
            get() = false

        override fun instantiateFragment(context: Context?, extras: IFragmentExtras) = HomeFragment().apply {
            arguments = Bundle().apply {
                putLong(context?.getString(R.string.bundle_fragment_id_key), extras.fragmentId)
            }
        }

    }

}