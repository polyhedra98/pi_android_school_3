package com.mishenka.notbasic.util

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mishenka.notbasic.R
import com.mishenka.notbasic.data.model.photo.OuterClass
import com.mishenka.notbasic.data.model.photo.SearchCallback
import com.mishenka.notbasic.home.HomeSearchParams
import com.mishenka.notbasic.util.Constants.PER_PAGE
import retrofit2.Response
import kotlin.math.max

abstract class PhotosSearchController(
    private val endlessPreferred: LiveData<Boolean>
) : PhotosSearchControllerI {

    abstract val TAG: String

    abstract fun getSearchResults(callback: SearchCallback, params: SearchParams,
                                  isContinuation: Boolean = false, page: Int = 1)

    val _responseAcquired = MutableLiveData<Event<Pair<Response<OuterClass?>, Boolean>>>()

    val _currentPage = MutableLiveData<Int>()

    val _lastPage = MutableLiveData<Int>()

    val _loading = MutableLiveData<Boolean>().apply { value = false }

    val _loadingContinuation = MutableLiveData<Boolean>().apply { value = false }

    val _resultsList = MutableLiveData<List<String>>().apply { value = emptyList() }

    val _resultsField = MutableLiveData<String>()

    var summary = ""

    var fullSummary = ""

    //TODO("Add 'is continuation'")
    val callback = object : SearchCallback {
        override fun onSearchCompleted(response: Response<OuterClass?>, isContinuation: Boolean) {
            _responseAcquired.value = Event(Pair(response, isContinuation))
            _loading.value = false
        }

        override fun onDataNotAvailable(message: String) {
            _resultsField.value = message
            _loading.value = false
        }
    }


    override fun endlessChanged(newValue: Boolean) {
        if (newValue && summary.isNotBlank()) {
            _resultsField.value = summary
        } else if (!newValue && fullSummary.isNotBlank()) {
            _resultsField.value = fullSummary
        }
    }


    override fun initStartingValues(context: Context) {
        _resultsField.value = context.getString(R.string.initial_empty_results)
    }

    override fun changePage(params: SearchParams, pageChange: Int) {
        if (_currentPage.value == null) {
            Log.i("NYA", "(from $TAG) currentPage is null")
            return
        }
        _loading.value = true
        Log.i("NYA", "(from $TAG) pageChange: $pageChange")
        if (pageChange == 0) {
            getSearchResults(callback = callback, params = params)
        } else {
            getSearchResults(callback = callback, params = params,
                page = _currentPage.value!! + pageChange)
        }
    }


    override fun continuousSearch(params: SearchParams) {
        Log.i("NYA", "(from $TAG) Getting results for page ${_currentPage.value!! + 1}")
        if (!_loadingContinuation.value!!) {
            _loadingContinuation.value = true
            getSearchResults(callback = callback, params = params,
                isContinuation = true, page = _currentPage.value!! + 1)
        }
    }


    override fun trimResults() {
        val length = _resultsList.value!!.size
        _resultsList.value = _resultsList.value!!
            .subList(max(length - PER_PAGE, 0), length)
    }


    override fun search(params: SearchParams) {
        _currentPage.value = null
        _lastPage.value = null
        _loading.value = true
        getSearchResults(callback = callback, params = params)
    }


    //TODO("Replace magical strings")
    override fun processSearchResult(
        context: Context,
        response: Response<OuterClass?>,
        isContinuation: Boolean
    ) {
        val code = response.code()
        with(context) {
            if (code != 200) {
                Log.i("NYA", "(from $TAG) Unsuccessful. Error code: $code")
                _resultsField.value = "Unsuccessful. Error code: $code"
                _resultsList.value = emptyList()
                return
            }
            if (response.body()!!.photos == null) {
                Log.i("NYA", "(from $TAG) Unsuccessful. Empty photos")
                _resultsField.value = "Unsuccessful. Empty photos."
                _resultsList.value = emptyList()
                return
            }
            val photos = response.body()!!.photos!!
            Log.i("NYA", "(from $TAG) Photos size: ${photos.photo!!.size}")
            summary = "Total: ${photos.total}"
            fullSummary = "Page: ${photos.page}\nPages: ${photos.pages}\nPer page: " +
                    "${photos.perpage}\nTotal: ${photos.total}"
            if (photos.pages != null && photos.pages == 0) {
                _currentPage.value = 0
            } else {
                _currentPage.value = photos.page
            }
            _lastPage.value = photos.pages
            if (photos.photo == null) {
                Log.i("NYA", "(from $TAG) Empty photo items")
                _resultsField.value = "Empty photo items."
                _resultsList.value = emptyList()
            } else {
                _resultsField.value = if (endlessPreferred.value!!) {
                    summary
                } else {
                    fullSummary
                }
                if (isContinuation) {
                    _resultsList.value = _resultsList.value!! + photos.photo!!.map { it.constructURL() }
                    _loadingContinuation.value = false
                } else {
                    _resultsList.value = photos.photo!!.map{ it.constructURL() }
                }
            }
        }
    }
}