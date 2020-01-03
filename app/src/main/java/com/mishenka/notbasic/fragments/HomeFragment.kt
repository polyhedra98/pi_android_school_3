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
import com.mishenka.notbasic.fragments.data.HomeFragmentData
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

    private var fragmentData: HomeFragmentData? = null

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
            fragmentData = (contentManager.getFragmentData(fragmentId!!) as HomeFragmentData?)

            if (fragmentData == null) {
                val initialData = object : HomeFragmentData() {
                    override var query: String? = null
                    override var currentPage: Int? = null
                }
                contentManager.registerFragment(fragmentId!!, initialData)
                fragmentData = initialData
            } else {
                Log.i("NYA_$TAG", "Restored fragment state. Query: ${fragmentData!!.query}, " +
                        "Page: ${fragmentData!!.currentPage}")
            }

            setupObservation(fragmentId!!)
        } else {
            Log.i("NYA_$TAG", "Fragment id is null, can't setup content.")
        }
    }


    private fun setupObservation(fragmentId: Long) {
        val observable = contentManager.getObservableForFragment(fragmentId)

        setupSearchButton(fragmentId)

        setupBasicViews()

        setupRecyclerView(observable)
    }


    private fun setupBasicViews() {
        fragmentData?.let { safeData ->
            safeData.query?.let { safeQuery ->
                search_et.setText(safeQuery)
            }
            safeData.currentPage?.let { safePage ->
                //TODO("Page setup")
            }
        }
    }


    private fun setupSearchButton(fragmentId: Long) {
        search_b.setOnClickListener {
            eventVM.requestData(object : IRequestData {

                override val extras = StdSearchExtras(
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
            updateFragmentData(data?.query, data?.data?.photos?.page)

            val photoList = constructUrlList(data?.data?.photos?.photo)
            (search_results_rv.adapter as HomeAdapter?)?.replaceItems(photoList)
            search_results_rv.scrollToPosition(0)
        })

    }


    private fun updateFragmentData(query: String?, page: Int?) {
        if (fragmentId == null) {
            Log.i("NYA_$TAG", "Error. Can't update data. Fragment id is null.")
            return
        }
        if (fragmentData == null) {
            fragmentData = object : HomeFragmentData() {
                override var query: String? = query
                override var currentPage: Int? = page
            }
        } else {
            fragmentData!!.query = query
            fragmentData!!.currentPage = page
        }
        contentManager.updateFragmentData(fragmentId!!, fragmentData!!)
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