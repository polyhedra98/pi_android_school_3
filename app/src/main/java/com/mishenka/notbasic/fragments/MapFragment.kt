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
import kotlinx.android.synthetic.main.fragment_temp_single_primary.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


class MapFragment : Fragment() {

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
        return inflater.inflate(R.layout.fragment_temp_single_primary, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        primary_add_primary_b.setOnClickListener {
            eventVM.requestFragment(HomeFragment.HomeFragmentRequest)
        }

        primary_add_primary_single_b.setOnClickListener {
            eventVM.requestFragment(MapFragmentRequest)
        }

        primary_main_tv.text = getString(R.string.fragment_single_primary_temp)

    }


    object MapFragmentRequest : IFragmentRequest {

        override val fragmentTag: String
            get() = "MAP_FRAG"

        override val navigationTitleId: Int
            get() = R.string.nav_map_title

        override val shouldBeDisplayedAlone: Boolean
            get() = true

        override val isSecondary: Boolean
            get() = false

        override val shouldHideToolbar: Boolean
            get() = false

        override fun instantiateFragment(context: Context?, extras: IFragmentExtras) = MapFragment().apply {
            arguments = Bundle().apply {
                putLong(context?.getString(R.string.bundle_fragment_id_key), extras.fragmentId)
            }
        }

    }


}