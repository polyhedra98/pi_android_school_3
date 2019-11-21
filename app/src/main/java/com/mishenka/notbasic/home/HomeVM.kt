package com.mishenka.notbasic.home

import android.content.Context
import android.util.Log
import android.widget.Button
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mishenka.notbasic.R
import com.mishenka.notbasic.data.model.photo.OuterClass
import com.mishenka.notbasic.data.model.photo.SearchCallback
import com.mishenka.notbasic.data.model.user.Favourite
import com.mishenka.notbasic.data.model.user.FavouriteSearch
import com.mishenka.notbasic.data.model.user.FavouriteToSearchToUser
import com.mishenka.notbasic.data.model.user.History
import com.mishenka.notbasic.data.source.AppRepository
import com.mishenka.notbasic.util.Event
import com.mishenka.notbasic.util.Validator
import com.mishenka.notbasic.util.Validator.validateFavAndCategoryId
import com.mishenka.notbasic.util.Validator.validateSearchAndUrl
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import retrofit2.Response
import java.lang.StringBuilder
import java.util.*

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

    private val _resultClicked = MutableLiveData<Event<Pair<String, String>>>()
    val resultClicked: LiveData<Event<Pair<String, String>>>
        get() = _resultClicked

    private val _resultsList = MutableLiveData<List<String>>().apply { value = emptyList() }
    val resultsList: LiveData<List<String>>
        get() = _resultsList

    private var query = ""

    var currentSearch: String? = null
        private set

    var currentUrl: String? = null
        private set

    var currentFavId: Long = -1
        private set

    var currentCategoryId: Long = -1
        private set


    fun setCurrentSearchAndUrl(search: String?, url: String?) {
        currentSearch = search
        currentUrl = url
    }

    fun setCurrentFavAndCategoryId(favId: Long?, categoryId: Long?) {
        currentFavId = favId ?: -1
        currentCategoryId = categoryId ?: -1
    }

    fun onResultClicked(url: String) {
        _resultClicked.value = Event(Pair(url, query))
    }

    fun search(userId: Long?) {
        val tempQuery = searchField.value?.toLowerCase()?.replace(" ", "_") ?: ""
        val validationResult = Validator.validateQuery(tempQuery)
        processValidationResult(validationResult)
        if (!validationResult) {
            return
        }
        query = tempQuery
        _currentPage.value = null
        _lastPage.value = null
        _loading.value = true
        appRepository.getSearchResults(query, SearchCallbackImplementation())
        userId?.let {
            viewModelScope.launch {
                appRepository.insertHistory(History(0, it, query, Date()))
            }
        }
    }

    fun toggleStar(userId: Long, buttonCallback: Button) {
        buttonCallback.isEnabled = false
        buttonCallback.isClickable = false
        //TODO("Change scope")
        GlobalScope.launch {
            val isAlreadyStarred = isAlreadyStarred(userId)
            Log.i("NYA", "Not starred yet.")
            val noError: Boolean
            noError = if (isAlreadyStarred) {
                unstar(userId)
            } else {
                star(userId)
            }
            MainScope().launch {
                if (noError) {
                    buttonCallback.text = if (isAlreadyStarred) {
                        buttonCallback.context!!.getString(R.string.star)
                    } else {
                        buttonCallback.context!!.getString(R.string.unstar)
                    }
                }
                Log.i("NYA", "No error: $noError")
                buttonCallback.isEnabled = true
                buttonCallback.isClickable = true
            }
        }
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

    suspend fun getUserHistory(userId: Long?) =
        if (userId == null) {
            null
        } else {
            appRepository.getHistoryByUserId(userId)
        }

    suspend fun isAlreadyStarred(userId: Long): Boolean {
        if (currentFavId == (-1).toLong() || currentCategoryId == (-1).toLong()) {
            Log.i("NYA", "Either fav or category id is null. " +
                    "Fav id: $currentFavId, Category id: $currentCategoryId")
            return false
        }
        return appRepository.getFSUid(userId, currentFavId, currentCategoryId) != null
    }

    suspend fun getFavIdByUrl(url: String) =
        appRepository.getFavIdByUrl(url)

    suspend fun getFavSearchIdByCategory(category: String) =
        appRepository.getFavSearchIdByCategory(category)

    private suspend fun star(userId: Long): Boolean {
        if(!validateSearchAndUrl(currentSearch, currentUrl)){
            Log.i("NYA", "Either current search, or url is null. " +
                    "Search: $currentSearch, Url: $currentUrl")
            return false
        }
        if (currentFavId == (-1).toLong()) {
            Log.i("NYA", "Fav id is -1. Inserting..")
            currentFavId = appRepository.insertFavourite(Favourite(0, currentUrl!!)) ?: -1
            if (currentFavId == (-1).toLong()) {
                Log.i("NYA", "Unexpected error. Fav id is -1 after an insert operation")
                return false
            }
        }
        if (currentCategoryId == (-1).toLong()) {
            Log.i("NYA", "Category id is -1. Inserting..")
            currentCategoryId = appRepository
                .insertFavouriteSearch(FavouriteSearch(0, currentSearch!!)) ?: -1
            if (currentCategoryId == (-1).toLong()) {
                Log.i("NYA", "Unexpected error. Category id is -1 after an insert operation")
                return false
            }
        }
        val FSUid: Long = appRepository
            .insertFSU(FavouriteToSearchToUser(0, userId, currentFavId, currentCategoryId)) ?: -1
        if (FSUid == (-1).toLong()) {
            Log.i("NYA", "Unexpected error. FSUid is -1 after an insert operation")
            return false
        }
        return true
    }

    private suspend fun unstar(userId: Long): Boolean {
        if(!validateSearchAndUrl(currentSearch, currentUrl)){
            Log.i("NYA", "Either current search, or url is null. " +
                    "Search: $currentSearch, Url: $currentUrl")
            return false
        }
        if (!validateFavAndCategoryId(currentFavId, currentCategoryId)) {
            Log.i("NYA", "Either current fav id, or category id is -1. " +
                    "Fav id: $currentFavId, Category id: $currentCategoryId")
            return false
        }
        appRepository.deleteFSUbyIds(userId, currentFavId, currentCategoryId)
        return true
    }

    private fun processSearchResult(response: Response<OuterClass?>): List<String> {
        val code = response.code()
        if (code != 200) {
            _resultsField.value = "Query: $query\nUnsuccessful. Error code: $code."
            return emptyList()
        }
        if (response.body()?.photos == null) {
            _resultsField.value = "Query: $query\nUnsuccessful. Empty photos."
            return emptyList()
        }
        val photos = response.body()!!.photos!!
        val builder = StringBuilder()
        builder.append("Query: $query\n")
        builder.append("Page: ${photos.page}\n")
            .append("Pages: ${photos.pages}\n")
            .append("Per page: ${photos.perpage}\n")
            .append("Total: ${photos.total}")
        if (photos.pages != null && photos.pages == 0) {
            _currentPage.value = 0
        } else {
            _currentPage.value = photos.page
        }
        _lastPage.value = photos.pages
        return if (photos.photo == null) {
            _resultsField.value = "Empty photo items."
            emptyList()
        } else {
            _resultsField.value = builder.toString()
            photos.photo!!.map { it.constructURL() }
        }
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

    inner class SearchCallbackImplementation :
        SearchCallback {
        override fun onSearchCompleted(response: Response<OuterClass?>) {
            _resultsList.value = processSearchResult(response)
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