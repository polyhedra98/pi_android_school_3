package com.mishenka.notbasic.home

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

class HomeVM private constructor(
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

    private val _currentPage = MutableLiveData<Int>()
    val currentPage: LiveData<Int>
        get() = _currentPage

    private val _lastPage = MutableLiveData<Int>()
    val lastPage: LiveData<Int>
        get() = _lastPage

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean>
        get() = _loading

    private var query = ""

    fun search() {
        query = searchField.value?.toLowerCase()?.replace(" ", "_") ?: ""
        val validationResult = Validator.validateQuery(query)
        processValidationResult(validationResult)
        if (!validationResult) {
            return
        }

        _currentPage.value = null
        _lastPage.value = null
        _loading.value = true
        appRepository.getSearchResults(query, SearchCallbackImplementation())
    }

    fun changePage(pageChange: Int) {
        if (currentPage.value == null) {
            return
        }
        _loading.value = true
        appRepository.getSearchResults(query, SearchCallbackImplementation(),
            currentPage.value!! + pageChange)
    }

    fun start(context: Context) {
        _resultsField.value = context.getString(R.string.initial_empty_results)
        _loading.value = false
    }

    private fun processSearchResult(response: Response<OuterClass?>): String {
        val code = response.code()
        if (code != 200) {
            return "Query: $query\nUnsuccessful. Error code: $code."
        }
        val photos = response.body()?.photos ?: return "Query: $query\nUnsuccessful. Empty photos."
        val builder = StringBuilder()
        builder.append("Query: $query\n")
        builder.append("Page: ${photos.page}\n")
            .append("Pages: ${photos.pages}\n")
            .append("Per page: ${photos.perpage}\n")
            .append("Total: ${photos.total}\n\n")
        if (photos.pages != null && photos.pages == 0) {
            _currentPage.value = 0
        } else {
            _currentPage.value = photos.page
        }
        _lastPage.value = photos.pages
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

    inner class SearchCallbackImplementation : SearchCallback {
        override fun onSearchCompleted(response: Response<OuterClass?>) {
            _resultsField.value = processSearchResult(response)
            _loading.value = false
        }

        override fun onDataNotAvailable(message: String) {
            _resultsField.value = message
            _loading.value = false
        }
    }

    companion object {

        private var INSTANCE: HomeVM? = null

        fun getInstance(appRepository: AppRepository) =
            INSTANCE ?: synchronized(HomeVM::class.java) {
                INSTANCE ?: HomeVM(
                    appRepository
                ).also { INSTANCE = it }
            }

    }

}