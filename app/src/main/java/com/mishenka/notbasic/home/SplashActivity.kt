package com.mishenka.notbasic.home

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.util.Log
import android.view.WindowManager
import androidx.appcompat.app.AppCompatDelegate.*
import androidx.core.content.ContextCompat
import com.mishenka.notbasic.R
import com.mishenka.notbasic.util.obtainAuthVM
import com.mishenka.notbasic.util.obtainHomeVM


class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        setupNightMode()

        prepareWindow()

        super.onCreate(savedInstanceState)

        invokeHandler()

        setContentView(R.layout.activity_splash)

        var userId: Long?

        obtainAuthVM().apply {
            start(this@SplashActivity)
            userId = this.userId.value
        }

        obtainHomeVM().apply {
            start(this@SplashActivity)

            if (getExternalStoragePermission()) {
                prefetchData(this@SplashActivity)
            } else {
                Log.i("NYA", "(from SplashActivity) WRITE_EXTERNAL_STORAGE permission denied")
            }

            if(userId != null) {
                Log.i("NYA", "User is not null. Pre-fetching.")
                prefetchUserData(userId!!)
            }
        }
    }


    private fun setupNightMode() {
        val nightModeString = PreferenceManager.getDefaultSharedPreferences(
            this
        ).getString(getString(R.string.settings_theme_key), "theme_empty")
        Log.i("NYA", "Night mode string: $nightModeString")
        val nightModeValue = when(nightModeString) {
            "theme_light" -> MODE_NIGHT_NO
            "theme_dark" -> MODE_NIGHT_YES
            else -> MODE_NIGHT_FOLLOW_SYSTEM
        }
        setDefaultNightMode(nightModeValue)
    }


    private fun getExternalStoragePermission() =
        ContextCompat.checkSelfPermission(
            this, Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED


    private fun prepareWindow() {
        supportActionBar?.hide()
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }

    private fun invokeHandler() {
        Handler().postDelayed({
            intent = Intent(this, HomeActivity::class.java)
            startActivity(intent).also {
                finish()
            }
        }, 2000)
    }

}
