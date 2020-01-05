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
import com.mishenka.notbasic.fragments.data.DetailFragmentData
import com.mishenka.notbasic.fragments.extras.DetailFragmentExtras
import com.mishenka.notbasic.interfaces.IFragmentExtras
import com.mishenka.notbasic.interfaces.IFragmentRequest
import com.mishenka.notbasic.managers.content.ContentManager
import com.mishenka.notbasic.viewmodels.EventVM
import kotlinx.android.synthetic.main.fragment_detail.*
import org.koin.android.ext.android.get
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class DetailFragment : Fragment() {

    private val TAG = "DetailFragment"


    private val eventVM by sharedViewModel<EventVM>()

    private val contentManager = get<ContentManager>()

    private var fragmentData: DetailFragmentData? = null

    private var fragmentId: Long? = null

    private var url: String? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        arguments?.run {
            fragmentId = getLong(getString(R.string.bundle_fragment_id_key))
            url = getString(getString(R.string.bundle_fragment_url_key))
        }
        return inflater.inflate(R.layout.fragment_detail, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (fragmentId != null) {
            fragmentData = (contentManager.getFragmentData(fragmentId!!) as DetailFragmentData?)

            if (fragmentData == null) {
                val initialData = object : DetailFragmentData() {
                    override var url: String? = this@DetailFragment.url
                }
                contentManager.registerFragment(fragmentId!!, initialData)
                fragmentData = initialData
            } else {
                Log.i("NYA_$TAG", "Restored fragment state. Url: ${fragmentData!!.url}")
            }

            setupViews()
        } else {
            Log.i("NYA_$TAG", "Fragment id is null, can't setup content.")
        }
    }


    private fun setupViews() {
        Glide.with(detail_photo_iv)
            .load(fragmentData!!.url)
            .fitCenter()
            .into(detail_photo_iv)

        detail_url_tv.text = fragmentData!!.url
        detail_category_tv.text = "CATEGORY (not yet implemented)."
    }


    object DetailFragmentRequest : IFragmentRequest {

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

        override fun instantiateFragment(context: Context?, extras: IFragmentExtras) = DetailFragment().apply {
            arguments = Bundle().apply {
                putLong(context?.getString(R.string.bundle_fragment_id_key), extras.fragmentId)
                putString(context?.getString(R.string.bundle_fragment_url_key),
                    (extras as? DetailFragmentExtras?)?.url)
            }
        }

    }

}