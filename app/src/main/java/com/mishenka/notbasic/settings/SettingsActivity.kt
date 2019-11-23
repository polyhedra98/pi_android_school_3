package com.mishenka.notbasic.settings

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
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


        companion object {

            fun newInstance() = SettingsFragment()

        }
    }
}