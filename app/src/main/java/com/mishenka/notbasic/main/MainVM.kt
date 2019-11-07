package com.mishenka.notbasic.main

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mishenka.notbasic.R
import com.mishenka.notbasic.data.model.OuterClass
import com.mishenka.notbasic.data.model.SearchCallback
import com.mishenka.notbasic.data.source.AppRepository
import com.mishenka.notbasic.util.Event
import com.mishenka.notbasic.util.Validator
import retrofit2.Response
import java.lang.StringBuilder

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

        appRepository.getSearchResults(query.toLowerCase(), object: SearchCallback {
            override fun onSearchCompleted(response: Response<OuterClass?>) {
                _resultsField.value = processSearchResult(response)
            }

            override fun onDataNotAvailable(message: String) {
                _resultsField.value = message
            }
        })
    }

    fun start(context: Context) {
        _resultsField.value = context.getString(R.string.initial_empty_results)
    }

    private fun processSearchResult(response: Response<OuterClass?>): String {
        val code = response.code()
        if (code != 200) {
            return "Unsuccessful. Error code: $code."
        }
        val photos = response.body()?.photos ?: return "Unsuccessful. Empty photos."
        val builder = StringBuilder()
        builder.append("Page: ${photos.page}\n")
            .append("Pages: ${photos.pages}\n")
            .append("Per page: ${photos.perpage}\n")
            .append("Total: ${photos.total}\n\n")
        val photoItems = photos.photo ?: return builder.append("Empty photo items.").toString()
        for (photoItem in photoItems) {
            builder.append("${photoItem.constructURL()}\n\n")
        }
        return builder.toString()
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