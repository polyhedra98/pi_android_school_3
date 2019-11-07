package com.mishenka.notbasic.main

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.inputmethod.InputMethodManager
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.mishenka.notbasic.R
import com.mishenka.notbasic.databinding.ActivityMainBinding
import com.mishenka.notbasic.util.Event
import com.mishenka.notbasic.util.Validator
import com.mishenka.notbasic.util.ViewModelFactory
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupBinding {
            mainVM = obtainVM().apply {
                start(this@MainActivity)

                search_b.setOnClickListener {
                    search()
                }
                search_results_tv.movementMethod = ScrollingMovementMethod()
                next_page_tv.setOnClickListener {
                    changePage(1)
                }
                prev_page_tv.setOnClickListener {
                    changePage(-1)
                }
                queryProcessed.observe(this@MainActivity, Observer<Event<Int>> {
                    it.getContentIfNotHandled()?.let { resultCode ->
                        processValidationResult(resultCode)
                    }
                })

            }
            lifecycleOwner = this@MainActivity
        }
    }


    private fun processValidationResult(resultCode: Int) {
        when(resultCode) {
            Validator.VALIDATION_RESULT_OK -> {
                Log.i("NYA", "Successful query validation")
                hideKeyboard()
            }
            Validator.VALIDATION_RESULT_ERROR -> {
                Log.i("NYA","Query validation error")
            }
            else -> {
                throw IllegalStateException("Illegal result code")
            }
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
