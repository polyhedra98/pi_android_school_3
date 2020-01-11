package com.mishenka.notbasic.fragments

import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.mishenka.notbasic.R
import com.mishenka.notbasic.data.model.FragmentExtras
import com.mishenka.notbasic.interfaces.IFragmentRequest
import com.mishenka.notbasic.interfaces.ISplashHost
import com.mishenka.notbasic.viewmodels.PrefVM
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


//TODO("Lock orientation in manifest, change minSdkVersion back to 17.")
class SplashFragment : Fragment() {

    private val TAG = "SplashFragment"


    private val prefVM by sharedViewModel<PrefVM>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity!!.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LOCKED

        return inflater.inflate(R.layout.fragment_splash, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        invokeHandler()

        prefVM.start(activity!!)
    }

    private fun invokeHandler() {
        Handler().postDelayed({
            (activity as ISplashHost?)?.mainContentRequested(true)
                ?.also {
                    activity!!.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                }
        }, 2000)
    }


    object SplashRequest : IFragmentRequest {

        override val fragmentTag: String
            get() = "SPL_FRAG"

        override val navigationTitleId: Int
            get() = R.string.nav_splash_title

        override val shouldBeDisplayedAlone: Boolean
            get() = true

        override val isSecondary: Boolean
            get() = false

        override val shouldHideToolbar: Boolean
            get() = true

        override fun instantiateFragment(context: Context, extras: FragmentExtras) = SplashFragment()

    }

}