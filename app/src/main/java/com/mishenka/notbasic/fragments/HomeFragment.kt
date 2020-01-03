package com.mishenka.notbasic.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mishenka.notbasic.R
import com.mishenka.notbasic.data.DataTypes
import com.mishenka.notbasic.data.models.StdSearchExtras
import com.mishenka.notbasic.data.models.StdSearchResponse
import com.mishenka.notbasic.data.models.photo.Photo
import com.mishenka.notbasic.fragments.adapters.HomeAdapter
import com.mishenka.notbasic.interfaces.*
import com.mishenka.notbasic.managers.content.ContentManager
import com.mishenka.notbasic.viewmodels.EventVM
import kotlinx.android.synthetic.main.fragment_home.*
import org.koin.android.ext.android.get
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


class HomeFragment : Fragment() {

    private val TAG = "HomeFragment"


    private val eventVM by sharedViewModel<EventVM>()

    private val contentManager = get<ContentManager>()

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

        if (fragmentId != null) {
            setupObservation(fragmentId!!)
        } else {
            Log.i("NYA_$TAG", "Fragment id is null, can't setup content.")
        }
    }


    private fun setupObservation(fragmentId: Long) {
        val observable = contentManager.getObservableForFragmentId(fragmentId)

        setupSearchButton(fragmentId)

        setupRecyclerView(observable)
    }


    private fun setupSearchButton(fragmentId: Long) {
        search_b.setOnClickListener {
            eventVM.requestData(object : IRequestData {

                override val extras = StdSearchExtras(
                    page = 1,
                    searchQuery = search_et.text?.toString()
                )

                override val ofType = DataTypes.STD_SEARCH

                override val fragmentId = fragmentId

            })
        }
    }


    private fun setupRecyclerView(observable: LiveData<IResponseData?>) {

        search_results_rv.layoutManager =
            LinearLayoutManager(context!!, RecyclerView.VERTICAL, false)
        search_results_rv.adapter = HomeAdapter(listOf("HEADER (not yet implemented)."), eventVM)

        observable.observe(this, Observer { response ->
            val data = (response as StdSearchResponse?)
            val photoList = constructUrlList(data?.data?.photos?.photo)

            Log.i("NYA_$TAG", "Observed photo list: $photoList")

            (search_results_rv.adapter as HomeAdapter?)?.replaceItems(photoList)
            search_results_rv.scrollToPosition(0)
        })

    }


    private fun constructUrlList(photos: List<Photo>?): List<String> {
        val photoList = ArrayList<String>()
        photos?.let { safePhotos ->
            for (photo in safePhotos) {
                photoList.add(photo.constructURL())
            }
        }
        return photoList
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