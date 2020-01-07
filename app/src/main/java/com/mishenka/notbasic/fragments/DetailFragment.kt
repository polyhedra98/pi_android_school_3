package com.mishenka.notbasic.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.mishenka.notbasic.R
import com.mishenka.notbasic.data.fragment.DetailFragmentData
import com.mishenka.notbasic.data.fragment.additional.DetailAdditionalExtras
import com.mishenka.notbasic.data.model.FragmentExtras
import com.mishenka.notbasic.interfaces.IFragmentRequest
import com.mishenka.notbasic.managers.preservation.PreservationManager
import com.mishenka.notbasic.viewmodels.EventVM
import kotlinx.android.synthetic.main.fragment_detail.*
import org.koin.android.ext.android.get
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class DetailFragment : Fragment() {

    private val TAG = "DetailFragment"


    private val eventVM by sharedViewModel<EventVM>()

    private val preservationManager = get<PreservationManager>()


    private var fragmentId: Long? = null

    private var restoredData: DetailFragmentData? = null

    private var categoryToPreserve: String? = null

    private var urlToPreserve: String? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentId = arguments?.getLong(getString(R.string.bundle_fragment_id_key))
        categoryToPreserve = arguments?.getString(getString(R.string.bundle_category_key))
        urlToPreserve = arguments?.getString(getString(R.string.bundle_url_key))

        if (fragmentId == null) {
            Log.i("NYA_$TAG", "Error. Fragment id is null.")
            throw Exception("Fragment id is null.")
        }
        return inflater.inflate(R.layout.fragment_detail, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        restoredData = (preservationManager.getDataForFragment(fragmentId!!) as? DetailFragmentData?)

        setupViews()
    }


    override fun onDestroyView() {
        preservationManager.preserveFragmentData(fragmentId!!,
            DetailFragmentData(
                category = categoryToPreserve,
                url = urlToPreserve
            )
        )

        super.onDestroyView()
    }


    private fun setupViews() {

        (restoredData?.category ?: categoryToPreserve)?.let { category ->
            setupCategoryRelatedViews(category)
        }

        (restoredData?.url ?: urlToPreserve)?.let { url ->
            setupUrlRelatedViews(url)
        }

    }


    private fun setupCategoryRelatedViews(category: String) {
        detail_category_tv.text = getString(R.string.category_ui, category)
    }


    private fun setupUrlRelatedViews(url: String) {
        detail_url_tv.text = getString(R.string.url_ui, url)

        Glide.with(detail_photo_iv)
            .load(url)
            .fitCenter()
            .into(detail_photo_iv)
    }


    object DetailRequest : IFragmentRequest {

        override val fragmentTag: String
            get() = "DETAIL_FRAG"

        override val navigationTitleId: Int
            get() = R.string.nav_detail_title

        override val shouldBeDisplayedAlone: Boolean
            get() = false

        override val isSecondary: Boolean
            get() = true

        override val shouldHideToolbar: Boolean
            get() = true

        override fun instantiateFragment(context: Context, extras: FragmentExtras) = DetailFragment()
            .apply {
                arguments = Bundle().apply {
                    putLong(context.getString(R.string.bundle_fragment_id_key), extras.fragmentId)
                    (extras.additionalExtras as? DetailAdditionalExtras)?.run {
                        putString(context.getString(R.string.bundle_category_key), category)
                        putString(context.getString(R.string.bundle_url_key), url)
                    }
                }
            }

    }

}