package com.mishenka.notbasic.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mishenka.notbasic.R
import com.mishenka.notbasic.interfaces.IFragmentExtras
import com.mishenka.notbasic.interfaces.IFragmentRequest
import com.mishenka.notbasic.viewmodels.EventVM
import kotlinx.android.synthetic.main.fragment_temp_secondary.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class DetailFragment : Fragment() {

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
        return inflater.inflate(R.layout.fragment_temp_secondary, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        secondary_add_secondary_b.setOnClickListener {
            eventVM.requestFragment(DetailFragmentRequest)
        }

        secondary_main_tv.text = getString(R.string.fragment_secondary_temp)
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
            }
        }

    }

}