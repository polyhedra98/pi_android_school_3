package com.mishenka.notbasic.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mishenka.notbasic.R
import com.mishenka.notbasic.data.DataTypes
import com.mishenka.notbasic.data.ErrorTypes
import com.mishenka.notbasic.data.models.StdSearchExtras
import com.mishenka.notbasic.data.models.StdSearchResponse
import com.mishenka.notbasic.data.models.photo.Photo
import com.mishenka.notbasic.fragments.adapters.HomeAdapter
import com.mishenka.notbasic.fragments.data.HomeFragmentData
import com.mishenka.notbasic.general.PagerFragment
import com.mishenka.notbasic.interfaces.*
import com.mishenka.notbasic.managers.content.ContentManager
import com.mishenka.notbasic.managers.content.Response
import com.mishenka.notbasic.viewmodels.EventVM
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.partial_results.view.*
import org.koin.android.ext.android.get
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


class HomeFragment : PagerFragment() {

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
                val initialData = invalidateFragmentData()
                contentManager.registerFragment(fragmentId!!, initialData)
                fragmentData = initialData
            } else {
                Log.i("NYA_$TAG", "Restored fragment state. Query: ${fragmentData!!.query}, " +
                        "Page: ${fragmentData!!.currentPage}")
            }

            setupViews(fragmentId!!)
        } else {
            Log.i("NYA_$TAG", "Fragment id is null, can't setup content.")
        }
    }


    override fun setupObservable(observable: LiveData<Response>) {
        observable.observe(this@HomeFragment, Observer { response ->
            when {
                response.error != null -> handleError(response.error)
                response.data == null -> {
                    invalidateFragmentData()
                    downloadStatusChanged(false)
                }
                else -> setupRecyclerView(response.data)
            }
            downloadStatusChanged(true)
        })
    }


    override fun initPageViews() {
        with(home_results_l) {
            prevPageView = prev_page_b
            nextPageView = next_page_b
        }
    }


    override fun setupPageViews() {
        prevPageView?.setOnClickListener {
            handlePageChange(-1)
        }
        nextPageView?.setOnClickListener {
            handlePageChange(1)
        }
    }


    private fun setupRecyclerView(responseData: IResponseData?) {
        with(home_results_l) {
            results_rv.layoutManager =
                LinearLayoutManager(context!!, RecyclerView.VERTICAL, false)
            results_rv.adapter =
                HomeAdapter(listOf("HEADER (not yet implemented)."), eventVM)


            val data = (responseData as StdSearchResponse?)
            updateFragmentData(
                data?.query,
                data?.data?.photos?.page,
                data?.data?.photos?.pages
            )
            pageChanged(data?.data?.photos?.page, data?.data?.photos?.pages)

            val photoList = constructUrlList(data?.data?.photos?.photo)
            (results_rv.adapter as HomeAdapter?)?.replaceItems(photoList)
            results_rv.scrollToPosition(0)
        }
    }


    private fun handleError(error: Pair<ErrorTypes, String>) {
        when(error.first) {
            ErrorTypes.VALIDATION_ERROR -> handleValidationError(error.second)
            ErrorTypes.RESPONSE_ERROR -> handleResponseError(error.second)
        }
    }


    private fun handleValidationError(msg: String) {
        Log.i("NYA_$TAG", "Validation error. $msg")
    }


    private fun handleResponseError(msg: String) {
        Log.i("NYA_$TAG", "Response error. $msg")
    }


    private fun setupViews(fragmentId: Long) {
        initPageViews()
        hidePageButtons()
        setupPageViews()

        val observable = contentManager.getObservableForFragment(fragmentId)

        setupSearchButton(fragmentId)

        setupBasicViews()

        setupObservable(observable)
    }


    private fun setupBasicViews() {
        fragmentData?.let { safeData ->
            safeData.query?.let { safeQuery ->
                search_et.setText(safeQuery)
            }
            pageChanged(safeData.currentPage, safeData.lastPage)
        }
    }


    private fun invalidateFragmentData() = object : HomeFragmentData() {
        override var query: String? = null
        override var currentPage: Int? = null
        override var lastPage: Int? = null
    }


    private fun handlePageChange(pageChange: Int) {
        if (fragmentData != null) {
            fragmentData!!.currentPage = fragmentData!!.currentPage!! + pageChange
            contentManager.updateFragmentData(fragmentId!!, fragmentData!!)

            eventVM.requestData(object : IRequestData {

                override val extras = StdSearchExtras(
                    fragmentData!!.query
                )

                override val ofType = DataTypes.STD_SEARCH

                override val fragmentId = this@HomeFragment.fragmentId!!

            })
        } else {
            Log.i("NYA_$TAG", "Can't handle page change. Fragment data is null.")
        }
    }


    private fun setupSearchButton(fragmentId: Long) {
        search_b.setOnClickListener {
            eventVM.requestFocusClear()

            contentManager.updateFragmentData(fragmentId, fragmentData!!)

            eventVM.requestData(object : IRequestData {

                override val extras = StdSearchExtras(
                    searchQuery = search_et.text?.toString()
                )

                override val ofType = DataTypes.STD_SEARCH

                override val fragmentId = fragmentId

            })
        }
    }


    private fun updateFragmentData(query: String?, page: Int?, lastPage: Int?) {
        if (fragmentId == null) {
            Log.i("NYA_$TAG", "Error. Can't update data. Fragment id is null.")
            return
        }
        if (fragmentData == null) {
            fragmentData = object : HomeFragmentData() {
                override var query: String? = query
                override var currentPage: Int? = page
                override var lastPage: Int? = lastPage
            }
        } else {
            fragmentData!!.query = query
            fragmentData!!.currentPage = page
            fragmentData!!.lastPage = lastPage
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