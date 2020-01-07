package com.mishenka.notbasic.fragments

import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mishenka.notbasic.R
import com.mishenka.notbasic.data.model.FragmentExtras
import com.mishenka.notbasic.interfaces.IFragmentRequest
import com.mishenka.notbasic.interfaces.ISplashHost


//TODO("Figure out what data to pre-fetch")
class SplashFragment : Fragment() {

    private val TAG = "SplashFragment"


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity!!.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        invokeHandler()
    }


    private fun invokeHandler() {
        Handler().postDelayed({
            (activity as ISplashHost).mainContentRequested(true)
                .also {
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