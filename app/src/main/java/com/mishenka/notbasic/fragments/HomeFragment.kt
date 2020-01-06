package com.mishenka.notbasic.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.mishenka.notbasic.R
import com.mishenka.notbasic.data.content.ContentType
import com.mishenka.notbasic.data.content.StdContentExtras
import com.mishenka.notbasic.data.content.StdContentResponse
import com.mishenka.notbasic.data.model.FragmentExtras
import com.mishenka.notbasic.fragments.data.HomeFragmentData
import com.mishenka.notbasic.fragments.data.StdPagerData
import com.mishenka.notbasic.interfaces.*
import com.mishenka.notbasic.managers.content.ContentManager
import com.mishenka.notbasic.managers.preservation.PreservationManager
import com.mishenka.notbasic.viewmodels.EventVM
import kotlinx.android.synthetic.main.fragment_home.*
import org.koin.android.ext.android.get
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


class HomeFragment : Fragment(), IPagerHost {

    private val TAG = "HomeFragment"


    private val eventVM by sharedViewModel<EventVM>()

    private val preservationManager = get<PreservationManager>()

    private val contentManager = get<ContentManager>()


    private var fragmentId: Long? = null

    private var restoredData: HomeFragmentData? = null

    private var searchFieldToPreserve: String? = null

    private var validationErrorToPreserve: String? = null

    private var pagerDataToPreserve: StdPagerData? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentId = arguments?.getLong(getString(R.string.bundle_fragment_id_key))
        if (fragmentId == null) {
            Log.i("NYA_$TAG", "Error. Fragment id is null.")
            throw Exception("Fragment id is null.")
        }
        return inflater.inflate(R.layout.fragment_home, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        restoredData = (preservationManager.getDataForFragment(fragmentId!!) as? HomeFragmentData?)

        setupViews()
    }


    override fun onDestroyView() {
        preserveSearchField()

        preserveValidationError()

        preservationManager.preserveFragmentData(fragmentId!!, HomeFragmentData(
            searchField = searchFieldToPreserve ?: restoredData?.searchField,
            validationError = validationErrorToPreserve,
            pagerData = pagerDataToPreserve ?: restoredData?.pagerData
        ))

        super.onDestroyView()
    }


    override fun pagerDataChanged(newData: IPagerData) {
        Log.i("NYA_$TAG", "Pager data has changed.")
        pagerDataToPreserve = (newData as? StdPagerData)
    }


    override fun pageChangeRequested(newPage: Int) {
        Log.i("NYA_$TAG", "Page #$newPage requested.")
        handleSearch(newPage)
    }


    override fun requestSetup() {
        Log.i("NYA_$TAG", "Pager setup requested.")
        val pagerData = pagerDataToPreserve ?: restoredData?.pagerData
        if (pagerData != null) {
            (childFragmentManager.findFragmentById(R.id.home_results_content_frame) as IPager)
                .updateData(pagerData)
        } else {
            Log.i("NYA_$TAG", "No pager data to restore.")
        }
    }


    private fun initResultsFragment() {
        childFragmentManager.beginTransaction().run {
            replace(R.id.home_results_content_frame, ResultsFragment())
            commit()
        }
    }


    private fun setupViews() {

        restoredData?.searchField?.let { safeSearchField ->
            home_search_et.setText(safeSearchField)

        }

        restoredData?.validationError?.let { safeError ->
            handleValidationError(safeError)
        }

        home_search_b.setOnClickListener {
            handleSearch()
        }

        initResultsFragment()

    }


    private fun handleSearch(argPage: Int? = null) {
        val query = if (argPage != null) {
            (pagerDataToPreserve?.query ?: restoredData?.pagerData?.query)!!
        } else {
            home_search_et.text.toString()
        }
        val validationErrorMsg = validateQuery(query)
        if (validationErrorMsg != null) {
            Log.i("NYA_$TAG", "Query $query validation failed. $validationErrorMsg")
            handleValidationError(validationErrorMsg)
            return
        }

        val page = argPage ?: 1

        val observable = contentManager.requestContent(
            ContentType.STD_TYPE,
            StdContentExtras(query, page))

        //TODO("Ok, I've just realized that I have to remove observer, once data is fetched. Memory leak!")
        observable.observe(this, Observer {
            (it as? StdContentResponse?)?.let { response ->
                conditionallyUpdateData(response)
            }
        })
    }


    private fun validateQuery(query: String): String? {
        return if (!query.matches(Regex("^[A-Za-z0-9_ ]*$"))) {
            getString(R.string.query_english_only)
        }
        else if (query.isBlank()) {
            getString(R.string.query_not_blank)
        }
        else {
            return null
        }
    }


    private fun handleValidationError(msg: String) {
        home_search_et.error = msg
    }


    private fun conditionallyUpdateData(response: StdContentResponse) {

        val photos = response.response.photos
        if (photos != null) {

            val currentPage = photos.page
            val lastPage = photos.pages
            val photo = photos.photo?.map { photo -> photo.constructURL() }

            if (currentPage != null && lastPage != null && photo != null) {

                val newData = object : StdPagerData() {
                    override val query: String = response.query
                    override val currentPage: Int = currentPage
                    override val lastPage: Int = lastPage
                    override val pagerList: List<String> = photo
                }

                pagerDataChanged(newData)

                (childFragmentManager.findFragmentById(R.id.home_results_content_frame) as IPager)
                    .updateData(newData)

            } else {
                Log.i("NYA_$TAG", "One of the important StdContentResponse elements is null. " +
                        "No data change notification.")
            }

        } else {
            Log.i("NYA_$TAG", "StdContentResponse Photos class is null")
        }

    }


    private fun preserveSearchField() {
        searchFieldToPreserve = home_search_et.text.toString()
    }


    private fun preserveValidationError() {
        validationErrorToPreserve = home_search_et.error?.toString()
    }


    object HomeRequest : IFragmentRequest {

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

        override fun instantiateFragment(context: Context, extras: FragmentExtras) = HomeFragment()
            .apply {
                arguments = Bundle().apply {
                    putLong(context.getString(R.string.bundle_fragment_id_key), extras.fragmentId)
                }
            }

    }

}