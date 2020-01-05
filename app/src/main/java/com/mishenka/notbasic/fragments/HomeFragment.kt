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
import com.mishenka.notbasic.data.content.StdContentResponse
import com.mishenka.notbasic.data.model.FragmentExtras
import com.mishenka.notbasic.fragments.data.HomeFragmentData
import com.mishenka.notbasic.interfaces.IContentExtras
import com.mishenka.notbasic.interfaces.IContentResponse
import com.mishenka.notbasic.interfaces.IFragmentRequest
import com.mishenka.notbasic.managers.content.ContentManager
import com.mishenka.notbasic.managers.preservation.PreservationManager
import com.mishenka.notbasic.viewmodels.EventVM
import kotlinx.android.synthetic.main.fragment_home.*
import org.koin.android.ext.android.get
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


class HomeFragment : Fragment() {

    private val TAG = "HomeFragment"


    private val eventVM by sharedViewModel<EventVM>()

    private val preservationManager = get<PreservationManager>()

    private val contentManager = get<ContentManager>()


    private var fragmentId: Long? = null

    private var restoredData: HomeFragmentData? = null

    private var searchFieldToPreserve: String? = null


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


    ////TODO("Change to smthing else (don't want to make 2 changes in 1 commit)")
    override fun onStop() {
        preservationManager.preserveFragmentData(fragmentId!!, HomeFragmentData(
            searchFieldToPreserve ?: restoredData?.searchField
        ))

        super.onStop()
    }


    private fun setupViews() {
        home_preserved_data_tv.text = if (restoredData?.searchField == null) {
            getString(R.string.no_data_to_restore)
        } else {
            getString(R.string.restored_data, restoredData!!.searchField)
        }

        home_search_b.setOnClickListener {
            tempPreservation()

            handleSearch()
        }
    }


    private fun handleSearch() {
        val observable = contentManager.requestContent(
            ContentType.STD_TYPE,
            object : IContentExtras{})

        observable.observe(this, Observer {
            (it as? StdContentResponse?)?.let { response ->
                Log.i("NYA_$TAG", "Observed the following data: ${response.responseList}")
            }
        })
    }


    private fun tempPreservation() {
        searchFieldToPreserve = home_search_et.text.toString()
        home_data_to_preserve_tv.text = getString(R.string.data_to_preserve, searchFieldToPreserve)
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