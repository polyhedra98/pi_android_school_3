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
import com.mishenka.notbasic.map.MapPhotosSearchController
import com.mishenka.notbasic.map.MapSearchParams
import com.mishenka.notbasic.util.Constants.PER_PAGE
import com.mishenka.notbasic.util.Constants.TYPE_HEADER
import com.mishenka.notbasic.util.Constants.TYPE_PHOTO
import com.mishenka.notbasic.util.Event
import com.mishenka.notbasic.util.PhotosSearchController
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


    //TODO("Since these values are basically the same, could probably make 2 separate VMs,
    // passing the controller to use. Don't have time though.. Maybe next week.")
    val responseAcquired: LiveData<Event<Pair<Response<OuterClass?>, Boolean>>>
        get() = homePhotosSearchController._responseAcquired

    val currentPage: LiveData<Int>
        get() = homePhotosSearchController._currentPage

    val lastPage: LiveData<Int>
        get() = homePhotosSearchController._lastPage

    val loading: LiveData<Boolean>
        get() = homePhotosSearchController._loading

    val loadingContinuation: LiveData<Boolean>
        get() = homePhotosSearchController._loadingContinuation

    val resultsList: LiveData<List<String>>
        get() = homePhotosSearchController._resultsList

    val resultsField: LiveData<String>
        get() = homePhotosSearchController._resultsField


    val mapResponseAcquired: LiveData<Event<Pair<Response<OuterClass?>, Boolean>>>
        get() = mapPhotosSearchController._responseAcquired

    val currentMapPage: LiveData<Int>
        get() = mapPhotosSearchController._currentPage

    val lastMapPage: LiveData<Int>
        get() = mapPhotosSearchController._lastPage

    val mapSearchLoading: LiveData<Boolean>
        get() = mapPhotosSearchController._loading

    val loadingMapContinuation: LiveData<Boolean>
        get() = mapPhotosSearchController._loadingContinuation

    val mapSearchResultsList: LiveData<List<String>>
        get() = mapPhotosSearchController._resultsList

    val mapResultsField: LiveData<String>
        get() = mapPhotosSearchController._resultsField

    private val _galleryResultsField = MutableLiveData<String>()
    val galleryResultsField: LiveData<String>
        get() = _galleryResultsField

    private val _galleryResultsList = MutableLiveData<List<String>>().apply { value = emptyList() }
    val galleryResultsList: LiveData<List<String>>
        get() = _galleryResultsList

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

    private val _resultClicked = MutableLiveData<Event<Pair<String, String>>>()
    val resultClicked: LiveData<Event<Pair<String, String>>>
        get() = _resultClicked

    private val _galleryItemClicked = MutableLiveData<Event<String>>()
    val galleryItemClicked: LiveData<Event<String>>
        get() = _galleryItemClicked

    private val _mapSearchClicked = MutableLiveData<Event<Pair<Double, Double>>>()
    val mapSearchClicked: LiveData<Event<Pair<Double, Double>>>
        get() = _mapSearchClicked

    private val _historyList = MutableLiveData<List<HistorySelectItem>>().apply { value = emptyList() }
    val historyList: LiveData<List<HistorySelectItem>>
        get() = _historyList

    private val _requestedFavDismiss = MutableLiveData<Event<Int>>()
    val requestedFavDismiss: LiveData<Event<Int>>
        get() = _requestedFavDismiss

    private val _requestGalDismiss = MutableLiveData<Event<Int>>()
    val requestedGalDismiss: LiveData<Event<Int>>
        get() = _requestGalDismiss

    private val _favouriteItems = MutableLiveData<MutableList<String>>()
        .apply { value = emptyList<String>().toMutableList() }
    val favouriteItems: LiveData<MutableList<String>>
        get() = _favouriteItems

    private val _favouriteItemsInfo = MutableLiveData<MutableList<Int>>()
        .apply { value = emptyList<Int>().toMutableList() }
    val favouriteItemsInfo: LiveData<MutableList<Int>>
        get() = _favouriteItemsInfo

    private var query = ""

    private var lat = ""

    private var lon = ""

    var currentSearch: String? = null
        private set

    var currentUrl: String? = null
        private set

    var currentFavId: Long = -1
        private set

    var currentCategoryId: Long = -1
        private set

    private val homePhotosSearchController = HomePhotosSearchController(appRepository, endlessPreferred)
    private val mapPhotosSearchController = MapPhotosSearchController(appRepository, endlessPreferred)


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


    fun onGalleryItemClicked(position: Int) {
        _galleryItemClicked.value = Event(galleryResultsList.value!![position])
    }


    fun onFavouriteClicked(position: Int) {
        _resultClicked.value = Event(
            Pair(
                favouriteItems.value!![position],
                getCategoryForPosition(position)
            )
        )
    }


    fun onMapSearchClicked(lat: Double, lng: Double) {
        _mapSearchClicked.value = Event(Pair(lat, lng))
    }


    fun requestFavouriteDismiss(position: Int) {
        _requestedFavDismiss.value = Event(position)
    }


    fun requestGalleryDismiss(position: Int) {
        _requestGalDismiss.value = Event(position)
    }


    fun dismissFavourite(userId: Long, position: Int) {
        val category = getCategoryForPosition(position)
        val url = favouriteItems.value!![position]
        favouriteItems.value!!.removeAt(position)
        favouriteItemsInfo.value!!.removeAt(position)
        //TODO("Change scope")
        GlobalScope.launch {
            appRepository.deleteFSUbyIds(
                userId,
                appRepository.getFavIdByUrl(url)!!,
                appRepository.getFavSearchIdByCategory(category)!!
            )
        }
    }


    fun getCategoryForPosition(position: Int): String {
        var pos = position
        while (favouriteItemsInfo.value!![pos] != TYPE_HEADER) {
            pos--
        }
        return favouriteItems.value!![pos]
    }


    fun prefetchData(userId: Long) {
        getFavourites(userId)
        getUserHistory(userId)
    }


    fun flashData() {
        _favouriteItems.value?.clear()
        _favouriteItemsInfo.value?.clear()
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
        homePhotosSearchController.initStartingValues(context)
        mapPhotosSearchController.initStartingValues(context)
        _galleryResultsField.value = context.getString(R.string.initial_gallery_results)

        val endlessPref = PreferenceManager.getDefaultSharedPreferences(context)
            ?.getBoolean(context.getString(R.string.settings_endless_list_key), false) ?: false
        _endlessPreferred.value = endlessPref
    }


    fun getFavourites(userId: Long?, action: (() -> Unit)? = null) {
        if (userId == null) {
            _favouriteItems.value?.clear()
            _favouriteItemsInfo.value?.clear()
            return
        }
        //TODO("Change scope")
        GlobalScope.launch {
            //TODO("Probably not the best way to group favs by categories")
            val dbFavList = appRepository.getFavourites(userId) ?: return@launch
            val favouritesItemsList = emptyList<String>().toMutableList()
            val favouritesItemsInfo = emptyList<Int>().toMutableList()
            var previousCategory: String? = null
            for (dbFavItem in dbFavList) {
                if (previousCategory != dbFavItem.category) {
                    previousCategory = dbFavItem.category
                    favouritesItemsList.add(previousCategory)
                    favouritesItemsInfo.add(TYPE_HEADER)
                }
                favouritesItemsList.add(dbFavItem.url)
                favouritesItemsInfo.add(TYPE_PHOTO)
            }
            MainScope().launch {
                _favouriteItems.value = favouritesItemsList
                _favouriteItemsInfo.value = favouritesItemsInfo
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
        homePhotosSearchController.endlessChanged(newValue)
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
        homePhotosSearchController.processSearchResult(context, response, isContinuation)
    }


    fun processMapSearchResult(context: Context, response: Response<OuterClass?>, isContinuation: Boolean) {
        mapPhotosSearchController.processSearchResult(context, response, isContinuation)
    }


    fun search(context: Context, userId: Long?) {
        val tempQuery = searchField.value?.toLowerCase()?.replace(" ", "_") ?: ""
        val validationResult = Validator.validateQuery(tempQuery)
        processValidationResult(context, validationResult)
        if (!validationResult) {
            return
        }
        query = tempQuery
        homePhotosSearchController.search(HomeSearchParams(query))
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
        mapPhotosSearchController.search(MapSearchParams(lat, lon))
        userId?.let {
            //TODO("Change scope")
            GlobalScope.launch {
                appRepository.insertHistory(History(0, it, "$lat/$lon", Date()))
            }
        }
    }


    fun continuousSearch() {
        homePhotosSearchController.continuousSearch(HomeSearchParams(query))
    }


    fun continuousMapSearch() {
        mapPhotosSearchController.continuousSearch(MapSearchParams(lat, lon))
    }


    fun changePage(pageChange: Int) {
        homePhotosSearchController.changePage(HomeSearchParams(query), pageChange)
    }


    fun changeMapPage(pageChange: Int) {
        mapPhotosSearchController.changePage(MapSearchParams(lat, lon), pageChange)
    }


    fun trimResultsList() {
        homePhotosSearchController.trimResults()
    }


    fun trimMapResultsList() {
        mapPhotosSearchController.trimResults()
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