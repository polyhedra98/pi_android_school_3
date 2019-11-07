package com.mishenka.notbasic.main

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.mishenka.notbasic.R
import com.mishenka.notbasic.databinding.ActivityMainBinding
import com.mishenka.notbasic.util.ViewModelFactory
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.show()

        setupBinding {
            mainVM = obtainVM().apply {
                start(this@MainActivity)

                search_b.setOnClickListener {
                    search()
                    search_et.clearFocus()
                    search_et.text.clear()
                    hideKeyboard()
                }
            }
            lifecycleOwner = this@MainActivity
        }
    }


    private fun hideKeyboard() {
        currentFocus?.let { safeCurrentFocus ->
            val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(safeCurrentFocus.windowToken, InputMethodManager.SHOW_FORCED)
        }
    }

    private fun obtainVM(): MainVM =
        ViewModelProviders.of(this, ViewModelFactory.getInstance()).get(MainVM::class.java)

    private fun setupBinding(action: ActivityMainBinding.() -> Unit) {
        DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main).apply {
            action()
        }
    }

}
