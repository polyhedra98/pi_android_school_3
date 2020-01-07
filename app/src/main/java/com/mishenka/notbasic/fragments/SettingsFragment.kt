package com.mishenka.notbasic.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.mishenka.notbasic.R
import com.mishenka.notbasic.data.model.FragmentExtras
import com.mishenka.notbasic.interfaces.IFragmentRequest
import com.mishenka.notbasic.viewmodels.EventVM
import com.mishenka.notbasic.viewmodels.PrefVM
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


class SettingsFragment : PreferenceFragmentCompat() {

    private val TAG = "SettingsFragment"


    private val prefVM by sharedViewModel<PrefVM>()

    private val eventVM by sharedViewModel<EventVM>()


    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupPreferences()
    }



    private fun setupPreferences() {

        val userPref = findPreference<Preference>(getString(R.string.settings_username_key))
        val authPref = findPreference<Preference>(getString(R.string.settings_auth_key))

        if (userPref != null && authPref != null) {
            prefVM.username.observe(this, Observer { username ->
                if (username == null) {

                    userPref.title = getString(R.string.username_anonymous_ui)
                    authPref.run {
                        title = getString(R.string.log_in_ui)
                        setOnPreferenceClickListener {
                            handleLogInClick()
                            true
                        }
                    }

                } else {

                    userPref.title = username
                    authPref.run {
                        title = getString(R.string.log_out_ui)
                        setOnPreferenceClickListener {
                            handleLogOutClick()
                            true
                        }
                    }

                }
            })
        }
        else {
            Log.i("NYA_$TAG", "Error. User / Auth pref is null.")
        }

    }


    private fun handleLogInClick() {
        eventVM.requestFragment(AuthFragment.AuthRequest, null)
    }


    private fun handleLogOutClick() {
        prefVM.logOut(context!!)
    }



    object SettingsRequest : IFragmentRequest {

        override val fragmentTag: String
            get() = "SETTINGS_FRAG"

        override val navigationTitleId: Int
            get() = R.string.nav_settings_title

        override val shouldBeDisplayedAlone: Boolean
            get() = false

        override val isSecondary: Boolean
            get() = false

        override val shouldHideToolbar: Boolean
            get() = false

        override fun instantiateFragment(context: Context, extras: FragmentExtras) = SettingsFragment()
            .apply {
                arguments = Bundle().apply {
                    putLong(context.getString(R.string.bundle_fragment_id_key), extras.fragmentId)
                }
            }
    }

}