package com.mishenka.notbasic.settings

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceFragmentCompat
import com.mishenka.notbasic.R
import com.mishenka.notbasic.util.replaceFragmentInActivity
import com.mishenka.notbasic.util.setupActionBar

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)

        setupActionBar(R.id.settings_tb) {
            setDisplayHomeAsUpEnabled(true)
        }

        setupViewFragment()
    }

    private fun setupViewFragment() {
        supportFragmentManager.findFragmentById(R.id.settings_content_frame)
            ?: replaceFragmentInActivity(R.id.settings_content_frame, SettingsFragment.newInstance())
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
        }

        companion object {

            fun newInstance() = SettingsFragment()

        }
    }
}