package com.mishenka.notbasic.main

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mishenka.notbasic.R
import com.mishenka.notbasic.data.source.AppRepository

class MainVM private constructor(
    private val appRepository: AppRepository
) : ViewModel() {

    private val _resultsField = MutableLiveData<String>()
    val resultsField: LiveData<String>
        get() = _resultsField

    val searchField = MutableLiveData<String>()


    fun search() {
        val query = searchField.value
        //TODO("Temp")
        val result = appRepository.getSearchResults(query).toString()
        Log.i("NYA", "Result: $result Query: $query")
        _resultsField.value = result
    }

    fun start(context: Context) {
        _resultsField.value = context.getString(R.string.initial_empty_results)
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