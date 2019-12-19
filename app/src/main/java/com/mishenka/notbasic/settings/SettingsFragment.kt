package com.mishenka.notbasic.settings

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.preference.CheckBoxPreference
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.mishenka.notbasic.R
import com.mishenka.notbasic.util.obtainAuthVM
import com.mishenka.notbasic.util.obtainHomeVM

class SettingsFragment : PreferenceFragmentCompat() {

    private val LOCATION_PERMISSION_REQUEST_CODE = 1


    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        (activity as AppCompatActivity).supportActionBar?.let { toolbar ->
            with(toolbar) {
                setTitle(R.string.title_activity_settings)
                setHomeAsUpIndicator(R.drawable.ic_arrow_back_24px)
            }
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupPreferences()
    }


    private fun setupPreferences() {
        val authVM = (activity as AppCompatActivity).obtainAuthVM()
        val homeVM = (activity as AppCompatActivity).obtainHomeVM()

        findPreference<CheckBoxPreference>(getString(R.string.settings_endless_list_key))
            ?.setOnPreferenceChangeListener { _, newValue ->
                homeVM.endlessChanged(newValue as Boolean)
                true
            }
        findPreference<ListPreference>(getString(R.string.settings_theme_key))
            ?.setOnPreferenceChangeListener { _, newValue ->
                themeChanged(newValue.toString())
                true
            }
        setupLocationPreference()
        val userPref = findPreference<Preference>(getString(R.string.settings_user_key))
        val authPref = findPreference<Preference>(getString(R.string.settings_auth_key))
        userPref?.let { safePref ->
            authVM.username.observe(this.viewLifecycleOwner, Observer { username ->
                if (username == null) {
                    safePref.title = getString(R.string.anonymous_user)
                    authPref?.let { authPref ->
                        authPref.title = getString(R.string.log_in)
                        authPref.setOnPreferenceClickListener {
                            authVM.requestLogIn()
                            true
                        }
                    }
                } else {
                    safePref.title = username
                    authPref?.let { authPref ->
                        authPref.title = getString(R.string.log_out)
                        authPref.intent = null
                        authPref.setOnPreferenceClickListener {
                            authVM.logOutUser(context!!)
                            true
                        }
                    }
                }
            })
        }
    }


    private fun themeChanged(newValue: String) {
        val nightModeValue = when(newValue) {
            "theme_light" -> AppCompatDelegate.MODE_NIGHT_NO
            "theme_dark" -> AppCompatDelegate.MODE_NIGHT_YES
            else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }
        AppCompatDelegate.setDefaultNightMode(nightModeValue)
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Log.i("NYA", "Permission has been denied")
                } else {
                    Log.i("NYA", "Permission has been accepted")
                    setupLocationPreference()
                }
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }


    private fun setupLocationPreference() {
        findPreference<Preference>(getString(R.string.settings_location_key))
            ?.let { safePreference ->
                with(safePreference) {
                    if (getFineLocationPermission()) {
                        title = getString(R.string.settings_location_permission_granted)
                        isSelectable = false
                    } else {
                        title = getString(R.string.settings_grant_location_permission)
                        isSelectable = true
                        setOnPreferenceClickListener {
                            requestPermission()
                            true
                        }
                    }
                }
            }
    }


    private fun getFineLocationPermission() =
        ContextCompat.checkSelfPermission(
            context!!, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED


    private fun requestPermission() {
        requestPermissions(
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }


    companion object {

        fun newInstance() = SettingsFragment()

    }

}