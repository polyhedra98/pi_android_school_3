package com.mishenka.notbasic.home

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.WindowManager
import com.mishenka.notbasic.R
import com.mishenka.notbasic.util.obtainAuthVM
import com.mishenka.notbasic.util.obtainHomeVM


class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
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

            if(userId != null) {
                Log.i("NYA", "User is not null. Pre-fetching.")
                prefetchData(userId!!)
            }
        }
    }


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
