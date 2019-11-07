package com.mishenka.notbasic.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import com.mishenka.notbasic.R

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        prepareWindow()

        invokeHandler()

        setContentView(R.layout.activity_splash)
    }


    private fun invokeHandler() {
        Handler().postDelayed({
            intent = Intent(this, MainActivity::class.java)
            startActivity(intent).also { finish() }
        }, 2000)
    }

    private fun prepareWindow() {
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }
}
