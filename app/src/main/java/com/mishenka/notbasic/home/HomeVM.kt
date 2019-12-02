package com.mishenka.notbasic.home

import android.content.Context
import android.util.Log
import android.widget.Button
import androidx.lifecycle.*
import androidx.preference.PreferenceManager
import com.mishenka.notbasic.R
import com.mishenka.notbasic.data.model.photo.OuterClass
import com.mishenka.notbasic.data.model.photo.SearchCallback
import com.mishenka.notbasic.data.model.user.*
import com.mishenka.notbasic.data.source.AppRepository
import com.mishenka.notbasic.util.Constants.PER_PAGE
import com.mishenka.notbasic.util.Event
import com.mishenka.notbasic.util.Validator
import com.mishenka.notbasic.util.Validator.validateFavAndCategoryId
import com.mishenka.notbasic.util.Validator.validateSearchAndUrl
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList

class HomeVM private constructor(
    private val appRepository: AppRepository
) : ViewModel() {

    //TODO("Helps with xml-fragment binding for placeholders")
    private val _temp = MutableLiveData<String>().apply { value = "Temp" }
    val temp: LiveData<String>
        get() = _temp

    private val _resultsField = MutableLiveData<String>()
    val resultsField: LiveData<String>
        get() = _resultsField

    private val _mapResultsField = MutableLiveData<String>()
    val mapResultsField: LiveData<String>
        get() = _mapResultsField

    private val _endlessPreferred = MutableLiveData<Boolean>()
    val endlessPreferred: LiveData<Boolean>
        get() = _endlessPreferred

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

    private val _currentMapPage = MutableLiveData<Int>()
    val currentMapPage: LiveData<Int>
        get() = _currentMapPage

    private val _lastPage = MutableLiveData<Int>()
    val lastPage: LiveData<Int>
        get() = _lastPage

    private val _lastMapPage = MutableLiveData<Int>()
    val lastMapPage: LiveData<Int>
        get() = _lastMapPage

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean>
        get() = _loading

    private val _mapSearchLoading = MutableLiveData<Boolean>()
    val mapSearchLoading: LiveData<Boolean>
        get() = _mapSearchLoading

    private val _loadingContinuation = MutableLiveData<Boolean>().apply { value = false }
    val loadingContinuation: LiveData<Boolean>
        get() = _loadingContinuation

    private val _loadingMapContinuation = MutableLiveData<Boolean>().apply { value = false }
    val loadingMapContinuation: LiveData<Boolean>
        get() = _loadingMapContinuation

    private val _resultClicked = MutableLiveData<Event<Pair<String, String>>>()
    val resultClicked: LiveData<Event<Pair<String, String>>>
        get() = _resultClicked

    private val _mapSearchClicked = MutableLiveData<Event<Pair<Double, Double>>>()
    val mapSearchClicked: LiveData<Event<Pair<Double, Double>>>
        get() = _mapSearchClicked

    private val _resultsList = MutableLiveData<List<String>>().apply { value = emptyList() }
    val resultsList: LiveData<List<String>>
        get() = _resultsList

    private val _mapSearchResultsList = MutableLiveData<List<String>>().apply { value = emptyList() }
    val mapSearchResultsList: LiveData<List<String>>
        get() = _mapSearchResultsList

    private val _historyList = MutableLiveData<List<HistorySelectItem>>().apply { value = emptyList() }
    val historyList: LiveData<List<HistorySelectItem>>
        get() = _historyList

    private val _favouritesList = MutableLiveData<ArrayList<FavouriteToShow>>()
        .apply { value = ArrayList() }
    val favouritesList: LiveData<ArrayList<FavouriteToShow>>
        get() = _favouritesList

    private val _responseAcquired = MutableLiveData<Event<Pair<Response<OuterClass?>, Boolean>>>()
    val responseAcquired: LiveData<Event<Pair<Response<OuterClass?>, Boolean>>>
        get() = _responseAcquired

    private val _mapResponseAcquired = MutableLiveData<Event<Pair<Response<OuterClass?>, Boolean>>>()
    val mapResponseAcquired: LiveData<Event<Pair<Response<OuterClass?>, Boolean>>>
        get() = _mapResponseAcquired

    private var query = ""

    private var lat = ""

    private var lon = ""

    private var fullSummary = ""

    private var mapFullSummary = ""

    private var summary = ""

    private var mapSummary = ""

    var currentSearch: String? = null
        private set

    var currentUrl: String? = null
        private set

    var currentFavId: Long = -1
        private set

    var currentCategoryId: Long = -1
        private set

    val TYPE_HEADER = 1
    val TYPE_CARD = 2

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


    fun onMapResultClicked(url: String) {
        _resultClicked.value = Event(Pair(url, "$lat/$lon"))
    }


    fun onFavouriteClicked(url: String, category: String) {
        _resultClicked.value = Event(Pair(url, category))
    }


    fun onMapSearchClicked(lat: Double, lng: Double) {
        _mapSearchClicked.value = Event(Pair(lat, lng))
    }


    fun dismissFavourite(userId: Long, position: Int) {
        val category = favouritesList.value!![getCategoryPosForPosition(position)].value
        val url = favouritesList.value!![position].value
        favouritesList.value!!.removeAt(position)
        //TODO("Change scope")
        GlobalScope.launch {
            appRepository.deleteFSUbyIds(
                userId,
                appRepository.getFavIdByUrl(url)!!,
                appRepository.getFavSearchIdByCategory(category)!!
            )
        }
    }


    fun getCategoryPosForPosition(position: Int): Int {
        var pos = position
        while (favouritesList.value!![pos].type != TYPE_HEADER) {
            pos--
        }
        return pos
    }


    fun prefetchData(userId: Long) {
        getFavourites(userId)
        getUserHistory(userId)
    }


    fun flashData() {
        _favouritesList.value = ArrayList()
        _historyList.value = emptyList()
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


    fun start(context: Context) {
        _resultsField.value = context.getString(R.string.initial_empty_results)
        _loading.value = false
        _mapResultsField.value = context.getString(R.string.initial_map_results)
        _mapSearchLoading.value = false

        appRepository.getRequestToken()

        val endlessPref = PreferenceManager.getDefaultSharedPreferences(context)
            ?.getBoolean(context.getString(R.string.settings_endless_list_key), false) ?: false
        _endlessPreferred.value = endlessPref
    }


    fun getFavourites(userId: Long?, action: (() -> Unit)? = null) {
        if (userId == null) {
            _favouritesList.value = ArrayList()
            return
        }
        //TODO("Change scope")
        GlobalScope.launch {
            //TODO("Probably not the best way to group favs by categories")
            val dbFavList = appRepository.getFavourites(userId) ?: return@launch
            val list: ArrayList<FavouriteToShow> = ArrayList(dbFavList.size + 1)
            var previousCategory: String? = null
            for (dbFavItem in dbFavList) {
                if (previousCategory != dbFavItem.category) {
                    previousCategory = dbFavItem.category
                    list.add(FavouriteToShow(previousCategory, TYPE_HEADER))
                    list.add(FavouriteToShow(dbFavItem.url, TYPE_CARD))
                } else {
                    list.add(FavouriteToShow(dbFavItem.url, TYPE_CARD))
                }
            }
            MainScope().launch {
                _favouritesList.value = list
                action?.invoke()
            }
        }
    }


    fun getUserHistory(userId: Long?, action: (() -> Unit)? = null) {
        if (userId == null) {
            _historyList.value = emptyList()
            return
        }
        //TODO("Change scope")
        GlobalScope.launch {
            val history = appRepository.getHistoryByUserId(userId)
            MainScope().launch {
                _historyList.value = history
                action?.invoke()
            }
        }
    }


    fun endlessChanged(newValue: Boolean) {
        if (newValue && summary.isNotBlank()) {
            _resultsField.value = summary
        } else if (!newValue && fullSummary.isNotBlank()) {
            _resultsField.value = fullSummary
        }
        _endlessPreferred.value = newValue
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


    fun processSearchResult(context: Context, response: Response<OuterClass?>, isContinuation: Boolean) {
        val code = response.code()
        with(context) {
            if (code != 200) {
                _resultsField.value = getString(R.string.query_error_code, query, code)
                _resultsList.value =  emptyList()
                return
            }
            if (response.body()?.photos == null) {
                _resultsField.value = getString(R.string.query_empty_photos, query)
                _resultsList.value = emptyList()
                return
            }
            val photos = response.body()!!.photos!!
            Log.i("NYA", "Photos size: ${photos.photo!!.size}")
            summary = getString(R.string.endless_query_header, query, photos.total)
            fullSummary = getString(R.string.query_header, query, photos.page,
                photos.pages, photos.perpage, photos.total)
            if (photos.pages != null && photos.pages == 0) {
                _currentPage.value = 0
            } else {
                _currentPage.value = photos.page
            }
            _lastPage.value = photos.pages
            if (photos.photo == null) {
                _resultsField.value = getString(R.string.empty_photo_items)
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
                    _resultsList.value = photos.photo!!.map { it.constructURL() }
                }
            }
        }
    }


    fun processMapSearchResult(context: Context, response: Response<OuterClass?>, isContinuation: Boolean) {
        val code = response.code()
        with(context) {
            if (code != 200) {
                _mapResultsField.value = getString(R.string.lat_lng_error_code, lat, lon, code)
                _mapSearchResultsList.value = emptyList()
                return
            }
            if (response.body()?.photos == null) {
                _mapResultsField.value = getString(R.string.lat_lng_empty_photos, lat, lon)
                _mapSearchResultsList.value = emptyList()
                return
            }
            val photos = response.body()!!.photos!!
            mapSummary = getString(R.string.endless_lat_lng_header, lat, lon, photos.total)
            mapFullSummary = getString(R.string.lat_lng_header, lat, lon, photos.page,
                photos.pages, photos.perpage, photos.total)
            if (photos.pages != null && photos.pages == 0) {
                _currentMapPage.value = 0
            } else {
                _currentMapPage.value = photos.page
            }
            _lastMapPage.value = photos.pages
            if (photos.photo == null) {
                _mapResultsField.value = getString(R.string.empty_photo_items)
                _mapSearchResultsList.value = emptyList()
            } else {
                _mapResultsField.value = if (endlessPreferred.value!!) {
                    summary
                } else {
                    fullSummary
                }
                if (isContinuation) {
                    _mapSearchResultsList.value = _mapSearchResultsList.value!! + photos.photo!!.map { it.constructURL() }
                    _loadingMapContinuation.value = false
                } else {
                    _mapSearchResultsList.value = photos.photo!!.map { it.constructURL() }
                }
            }
        }
    }


    fun search(context: Context, userId: Long?) {
        val tempQuery = searchField.value?.toLowerCase()?.replace(" ", "_") ?: ""
        val validationResult = Validator.validateQuery(tempQuery)
        processValidationResult(context, validationResult)
        if (!validationResult) {
            return
        }
        query = tempQuery
        _currentPage.value = null
        _lastPage.value = null
        _loading.value = true
        appRepository.getSearchResults(query, SearchCallbackImplementation())
        userId?.let {
            //TODO("Change scope")
            GlobalScope.launch {
                appRepository.insertHistory(History(0, it, query, Date()))
            }
        }
    }


    fun mapSearch(context: Context, lat: String, lon: String, userId: Long?) {
        this.lat = lat
        this.lon = lon
        _currentMapPage.value = null
        _lastMapPage.value = null
        _mapSearchLoading.value = true
        appRepository.getMapSearchResults(this.lat, this.lon, MapSearchCallbackImplementation())
        userId?.let {
            //TODO("Change scope")
            GlobalScope.launch {
                appRepository.insertHistory(History(0, it, "$lat/$lon", Date()))
            }
        }
    }


    fun continuousSearch() {
        Log.i("NYA", "Getting results for page ${currentPage.value!! + 1}")
        if (!_loadingContinuation.value!!) {
            _loadingContinuation.value = true
            appRepository.getSearchResults(query, SearchCallbackImplementation(true)
                ,currentPage.value!! + 1)
        }
    }


    fun continuousMapSearch() {
        Log.i("NYA", "Getting results for page ${currentMapPage.value!! + 1}")
        if (!_loadingMapContinuation.value!!) {
            _loadingMapContinuation.value = true
            appRepository.getMapSearchResults(lat, lon, MapSearchCallbackImplementation(true),
                currentMapPage.value!! + 1)
        }
    }


    fun changePage(pageChange: Int) {
        if (currentPage.value == null) {
            return
        }
        _loading.value = true
        if (pageChange == 0) {
            appRepository.getSearchResults(query, SearchCallbackImplementation())
        } else {
            appRepository.getSearchResults(query, SearchCallbackImplementation(),
                currentPage.value!! + pageChange)
        }
    }


    fun changeMapPage(pageChange: Int) {
        if (currentMapPage.value == null) {
            return
        }
        _mapSearchLoading.value = true
        if (pageChange == 0) {
            appRepository.getMapSearchResults(lat, lon, MapSearchCallbackImplementation())
        } else {
            appRepository.getMapSearchResults(lat, lon, MapSearchCallbackImplementation(),
                currentMapPage.value!! + pageChange)
        }
    }


    fun trimResultsList() {
        val length = _resultsList.value!!.size
        _resultsList.value = _resultsList.value!!.subList(length - PER_PAGE, length)
    }


    fun trimMapResultsList() {
        val length = _mapSearchResultsList.value!!.size
        _mapSearchResultsList.value = _mapSearchResultsList.value!!.subList(length - PER_PAGE, length)
    }


    private fun processValidationResult(context: Context, validationResult: Boolean) {
        _queryProcessed.value = if (validationResult) {
            Event(Validator.VALIDATION_RESULT_OK)
        } else {
            Event(Validator.VALIDATION_RESULT_ERROR)
        }
        _observableError.value = if(validationResult) {
            null
        } else {
            context.getString(R.string.english_or_digits_only)
        }
    }


    inner class SearchCallbackImplementation(
        private val isContinuation: Boolean? = null
    ) : SearchCallback {
        override fun onSearchCompleted(response: Response<OuterClass?>) {
            _responseAcquired.value = Event(Pair(response, isContinuation ?: false))
            _loading.value = false
        }

        override fun onDataNotAvailable(message: String) {
            _resultsField.value = message
            _loading.value = false
        }
    }


    inner class MapSearchCallbackImplementation(
        private val isContinuation: Boolean? = null
    ) : SearchCallback {
        override fun onSearchCompleted(response: Response<OuterClass?>) {
            _mapResponseAcquired.value = Event(Pair(response, isContinuation ?: false))
            _mapSearchLoading.value = false
        }

        override fun onDataNotAvailable(message: String) {
            _mapResultsField.value = message
            _mapSearchLoading.value = false
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