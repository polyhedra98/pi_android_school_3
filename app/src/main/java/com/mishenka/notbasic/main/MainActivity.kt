package com.mishenka.notbasic.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.mishenka.notbasic.R
import com.mishenka.notbasic.databinding.ActivityMainBinding
import com.mishenka.notbasic.util.ViewModelFactory

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main).apply {
            mainVM = obtainVM().apply {
                setHelloWorld("Hello World")
            }
        }
    }


    private fun obtainVM(): MainVM =
        ViewModelProviders.of(this, ViewModelFactory.getInstance()).get(MainVM::class.java)

}
