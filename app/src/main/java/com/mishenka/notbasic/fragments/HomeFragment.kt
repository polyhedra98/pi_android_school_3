package com.mishenka.notbasic.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mishenka.notbasic.R
import com.mishenka.notbasic.managers.navigation.NavigationManager
import com.mishenka.notbasic.interfaces.IFragmentExtras
import com.mishenka.notbasic.interfaces.IFragmentRequest
import com.mishenka.notbasic.viewmodels.EventVM
import kotlinx.android.synthetic.main.fragment_temp_primary.*
import org.koin.android.ext.android.get
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


class HomeFragment : Fragment() {

    private val eventVM by sharedViewModel<EventVM>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_temp_primary, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        primary_add_secondary_b.setOnClickListener {
            eventVM.requestFragment(DetailFragment.DetailRequest)
        }

        primary_add_primary_b.setOnClickListener {
            eventVM.requestFragment(HomeRequest)
        }

        primary_add_primary_single_b.setOnClickListener {
            eventVM.requestFragment(MapFragment.MapRequest)
        }

        primary_main_tv.text = getString(R.string.fragment_primary_temp)
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

        override fun instantiateFragment(extras: IFragmentExtras?) = HomeFragment()

    }

}