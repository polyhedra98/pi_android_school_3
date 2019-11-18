package com.mishenka.notbasic.settings

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.mishenka.notbasic.R
import com.mishenka.notbasic.databinding.ActivityAuthBinding
import com.mishenka.notbasic.util.obtainAuthVM

class AuthActivity : AppCompatActivity(), AuthCallback {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DataBindingUtil.setContentView<ActivityAuthBinding>(this, R.layout.activity_auth).apply {

            authVM = obtainAuthVM()

            authVM?.loginError?.observe(this@AuthActivity, Observer {
                it.getContentIfNotHandled()?.let { error ->
                    authUsernameEt.error = error
                }
            })

            authLoginB.setOnClickListener {
                val username =  authUsernameEt.text.toString()
                authVM?.logInUser(username, this@AuthActivity, this@AuthActivity)
            }

            authCreateB.setOnClickListener {
                val username = authUsernameEt.text.toString()
                authVM?.createUser(username, this@AuthActivity, this@AuthActivity)
            }

        }
    }


    override fun onAuthenticationFinished() {
        finish()
    }


}
