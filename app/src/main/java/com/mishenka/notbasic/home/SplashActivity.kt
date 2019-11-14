package com.mishenka.notbasic.home

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import com.mishenka.notbasic.R

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        prepareWindow()

        super.onCreate(savedInstanceState)

        invokeHandler()

        setContentView(R.layout.activity_splash)
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
