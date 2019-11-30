package com.mishenka.notbasic.settings

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.preference.CheckBoxPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.mishenka.notbasic.R
import com.mishenka.notbasic.util.*


class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        setupActionBar(R.id.settings_tb) {
            setDisplayHomeAsUpEnabled(true)
        }

        obtainAuthVM().apply {

            userLogIn.observe(this@SettingsActivity, Observer<Event<Long>> {
                it.getContentIfNotHandled()?.let { safeId ->
                    obtainHomeVM().prefetchData(safeId)
                }
            })

            userLogOut.observe(this@SettingsActivity, Observer<Event<Unit>> {
                it.getContentIfNotHandled()?.let {
                    obtainHomeVM().flashData()
                }
            })

        }

        setupViewFragment()
    }


    override fun onOptionsItemSelected(item: MenuItem) =
        when(item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }


    private fun setupViewFragment() {
        supportFragmentManager.findFragmentById(R.id.settings_content_frame)
            ?: replaceFragmentInActivity(R.id.settings_content_frame, SettingsFragment.newInstance())
    }


    class SettingsFragment : PreferenceFragmentCompat() {

        private val LOCATION_PERMISSION_REQUEST_CODE = 1


        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
        }


        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)

            setupPreferences()
        }


        private fun setupPreferences() {
            val authVM = (activity as AppCompatActivity).obtainAuthVM()
            val homeVM = (activity as AppCompatActivity).obtainHomeVM()

            findPreference<CheckBoxPreference>(getString(R.string.settings_endless_list_key))
                ?.setOnPreferenceChangeListener { preference, newValue ->
                    homeVM.endlessChanged(newValue as Boolean)
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
                            authPref.intent = Intent(activity, AuthActivity::class.java)
                            authPref.onPreferenceClickListener = null
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
}