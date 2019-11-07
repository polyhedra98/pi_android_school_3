package com.mishenka.notbasic.main

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mishenka.notbasic.R
import com.mishenka.notbasic.data.source.AppRepository
import com.mishenka.notbasic.util.Event
import com.mishenka.notbasic.util.Validator

class MainVM private constructor(
    private val appRepository: AppRepository
) : ViewModel() {

    private val _resultsField = MutableLiveData<String>()
    val resultsField: LiveData<String>
        get() = _resultsField

    val searchField = MutableLiveData<String>()

    private val _queryProcessed = MutableLiveData<Event<Int>>()
    val queryProcessed: LiveData<Event<Int>>
        get() = _queryProcessed

    private val _observableError = MutableLiveData<String?>()
    val observableError: LiveData<String?>
        get() = _observableError

    fun search() {
        val query = searchField.value ?: ""
        val validationResult = Validator.validateQuery(query)
        processValidationResult(validationResult)
        if (!validationResult) {
            return
        }
        //TODO("Temp")
        val result = appRepository.getSearchResults(query).toString()
        Log.i("NYA", "Result: $result Query: $query")
        _resultsField.value = result
    }

    fun start(context: Context) {
        _resultsField.value = context.getString(R.string.initial_empty_results)
    }

    private fun processValidationResult(validationResult: Boolean) {
        _queryProcessed.value = if (validationResult) {
            Event(Validator.VALIDATION_RESULT_OK)
        } else {
            Event(Validator.VALIDATION_RESULT_ERROR)
        }
        _observableError.value = if(validationResult) {
            null
        } else {
            "English / Digits only"
        }
    }

    companion object {

        private var INSTANCE: MainVM? = null

        fun getInstance(appRepository: AppRepository) =
            INSTANCE ?: synchronized(MainVM::class.java) {
                INSTANCE ?: MainVM(
                    appRepository
                ).also { INSTANCE = it }
            }

    }

}