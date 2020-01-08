package com.mishenka.notbasic.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mishenka.notbasic.R
import com.mishenka.notbasic.data.model.FragmentExtras
import com.mishenka.notbasic.interfaces.IFragmentRequest
import com.mishenka.notbasic.viewmodels.PrefVM
import kotlinx.android.synthetic.main.fragment_history.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


class HistoryFragment : Fragment() {

    private val TAG = "HistoryFragment"


    private val prefVM by sharedViewModel<PrefVM>()


    private var fragmentId: Long? = null


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

        return inflater.inflate(R.layout.fragment_history, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViews()
    }



    private fun setupViews() {

        prefVM.username.run {
            if (value == null) {
                history_upper_info_tv.text = getString(R.string.history_anonymous_ui)
            } else {
                history_upper_info_tv.text = getString(R.string.history_ui, value)
            }
        }

    }



    object HistoryRequest : IFragmentRequest {

        override val fragmentTag: String
            get() = "HISTORY_FRAG"

        override val navigationTitleId: Int
            get() = R.string.nav_history_title

        override val shouldBeDisplayedAlone: Boolean
            get() = true

        override val isSecondary: Boolean
            get() = false

        override val shouldHideToolbar: Boolean
            get() = false

        override fun instantiateFragment(context: Context, extras: FragmentExtras) = HistoryFragment()
            .apply {
                arguments = Bundle().apply {
                    putLong(context.getString(R.string.bundle_fragment_id_key), extras.fragmentId)
                }
            }
    }


}