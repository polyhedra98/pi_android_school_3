package com.mishenka.notbasic.viewmodels

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.preference.PreferenceManager
import com.mishenka.notbasic.R
import com.mishenka.notbasic.data.model.user.*
import com.mishenka.notbasic.data.source.AppDatabase
import com.mishenka.notbasic.utils.date.DateConverter
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import java.io.File
import java.net.URL
import java.util.*


val prefsModule = module {
    viewModel { PrefVM(get()) }
}


class PrefVM(
    private val appDatabase: AppDatabase
): ViewModel() {

    private val TAG = "PrefVM"


    private val _username = MutableLiveData<String?>().apply { value = null }
    val username: LiveData<String?>
        get() = _username


    private val _userId = MutableLiveData<Long?>().apply { value = null }
    val userId: LiveData<Long?>
        get() = _userId


    private val _powerNotificationsPreferred = MutableLiveData<Boolean>()
    val powerNotificationsPreferred: LiveData<Boolean>
        get() = _powerNotificationsPreferred


    private var lastObtainedUri: Uri? = null



    fun start(context: Context) {
        val prefUserData = prefGetUser(context)
        _username.value = prefUserData?.username
        _userId.value = prefUserData?.id

        setupPowerPreference(context)
    }


    fun setupPowerPreference(context: Context) {
        val prefPower = PreferenceManager.getDefaultSharedPreferences(context)
            ?.getBoolean(context.getString(R.string.settings_power_key), false) ?: false
        _powerNotificationsPreferred.value = prefPower
    }


    fun powerNotificationPrefChanged(newValue: Boolean) {
        _powerNotificationsPreferred.value = newValue
    }


    fun logIn(context: Context, username: String) {
        //TODO("Change scope")
        GlobalScope.launch {
            val id = appDatabase.userDao().getUserIdByUsername(username)
            if (id != null) {
                MainScope().launch {
                    _username.value = username
                    _userId.value = id
                    prefSaveUser(context, PrefUserData(username, id))
                }
            }
        }
    }


    fun logIn(context: Context, prefUserData: PrefUserData) {
        _username.value = prefUserData.username
        _userId.value = prefUserData.id
        prefSaveUser(context, prefUserData)
    }


    fun logOut(context: Context) {
        _username.value = null
        _userId.value = null
        prefDeleteUser(context)
    }


    fun signUp(context: Context, username: String) {
        //TODO("Change scope")
        GlobalScope.launch {
            val id = appDatabase.userDao().insertUser(User(0, username))
            if (id == null) {
                Log.i("NYA_$TAG", "Error inserting user. Id is null.")
            }
            else {
                MainScope().launch {
                    logIn(context, PrefUserData(username, id))
                }
            }
        }
    }


    fun userExists(username: String): LiveData<Boolean> {
        val observable = MutableLiveData<Boolean>()
        //TODO("Change scope")
        GlobalScope.launch {
            val id = appDatabase.userDao().getUserIdByUsername(username)
            MainScope().launch {
                observable.value = (id != null)
            }
        }
        return observable
    }


    fun conditionallySaveSearch(search: String, page: Int) {
        val userIdConstant = userId.value
        if (userIdConstant != null) {
            val timeStamp = Date()

            Log.i("NYA_$TAG", "Saving search $search for " +
                    "userId ${userId.value}. Time: $timeStamp")
            //TODO("Change scope")
            GlobalScope.launch {
                appDatabase.userDao().insertHistory(
                    History(
                        0,
                        userIdConstant,
                        search,
                        page,
                        timeStamp
                    )
                )
            }
        }
    }


    //TODO("Why would I put this here? Might have to refactor later.")
    fun isAlreadyStarred(userId: Long, category: String, url: String): LiveData<Boolean> {
        val observable = MutableLiveData<Boolean>()

        //TODO("Change scope")
        GlobalScope.launch {
            val categoryId = appDatabase.userDao().getFavSearchIdByCategory(category)

            if (categoryId == null) {
                Log.i("NYA_$TAG", "Not yet starred. Category id is null.")
                MainScope().launch {
                    observable.value = false
                }
            }
            else {
                val urlId = appDatabase.userDao().getFavIdByUrl(url)

                if (urlId == null) {
                    Log.i("NYA_$TAG", "Not yet starred. Url id is null.")
                    MainScope().launch {
                        observable.value = false
                    }
                }
                else {
                    val fsuId = appDatabase.userDao().getFavToSearchToUserId(
                        userId, urlId, categoryId
                    )

                    if (fsuId == null) {
                        Log.i("NYA_$TAG", "Not yet starred. FSU id is null.")
                        MainScope().launch {
                            observable.value = false
                        }
                    }
                    else {
                        Log.i("NYA_$TAG", "Already starred.")
                        MainScope().launch {
                            observable.value = true
                        }
                    }
                }
            }
        }

        return observable
    }


    fun toggleStar(isAlreadyStarred: Boolean, userId: Long, category: String,
                   url: String, before: (() -> Unit)?, after: (() -> Unit)?) {

        before?.invoke()

        //TODO("Change scope")
        GlobalScope.launch {
            if (isAlreadyStarred) {
                val categoryId = appDatabase.userDao().getFavSearchIdByCategory(category)

                if (categoryId == null) {
                    Log.i("NYA_$TAG", "Error unstarring. Category id is null.")
                }
                else {
                    val urlId = appDatabase.userDao().getFavIdByUrl(url)

                    if (urlId == null) {
                        Log.i("NYA_$TAG", "Error unstarring. Url id is null.")
                    }
                    else {
                        val fsuId = appDatabase.userDao().getFavToSearchToUserId(
                            userId, urlId, categoryId
                        )

                        if (fsuId == null) {
                            Log.i("NYA_$TAG", "Error unstarring. FSU id is null.")
                        }
                        else {
                            Log.i("NYA_$TAG", "No issues. Unstarring.")
                            appDatabase.userDao().deleteFavToSearchToUserByIds(
                                userId, urlId, categoryId
                            )
                        }
                    }
                }

                MainScope().launch {
                    after?.invoke()
                }
            }
            else {
                var categoryId = appDatabase.userDao().getFavSearchIdByCategory(category)

                if (categoryId == null) {
                    Log.i("NYA_$TAG", "Category id is null. Inserting.")
                    categoryId = appDatabase.userDao().insertFavSearch(FavouriteSearch(0, category))
                }

                var urlId = appDatabase.userDao().getFavIdByUrl(url)

                if (urlId == null) {
                    Log.i("NYA_$TAG", "Url id is null. Inserting.")
                    urlId = appDatabase.userDao().insertFav(Favourite(0, url))
                }

                appDatabase.userDao().insertFavToSearchToUser(FavouriteToSearchToUser(
                    0, userId, urlId!!, categoryId!!
                ))?.also {
                    Log.i("NYA_$TAG", "Successfully starred.")
                }

                MainScope().launch {
                    after?.invoke()
                }
            }
        }

    }
    //TODO("~Why would I put this here? Might have to refactor later.")


    //TODO("Why would I put this here (Part 2)? Might have to refactor later.")
    fun downloadPhoto(context: Context, urlString: String,
                      before: (() -> Unit)?, after: (() -> Unit)?) {
        before?.invoke()
        //TODO("Change scope")
        GlobalScope.launch {
            val url = URL(urlString)
            val connection = url.openConnection()
            connection.doInput = true
            connection.connect()
            val inputStream = connection.getInputStream()
            val downloadedBitmap = BitmapFactory.decodeStream(inputStream)
            MainScope().launch {
                savePhoto(context, downloadedBitmap, after)
            }
        }
    }


    private fun savePhoto(context: Context, bitmap: Bitmap, after: (() -> Unit)?) {
        val uri = MediaStore.Images.Media.insertImage(context.contentResolver, bitmap,
            "not_basic_${Date().time}", "")
        after?.invoke()
        Log.i("NYA_$TAG", "Saved bitmap. Uri: $uri")
    }


    fun deletePhoto(context: Context, uri: String, after: (() -> Unit)?) {
        val projection = arrayOf(MediaStore.Images.Media._ID)
        val selection = MediaStore.Images.Media.DATA + " = ?"
        val selectionArgs = arrayOf(uri)
        val cr = context.contentResolver

        val cursor = cr.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            null
        )

        cursor?.let { safeCursor ->
            if (safeCursor.moveToFirst()) {
                val id = safeCursor.getLong(safeCursor.getColumnIndex(MediaStore.Images.Media._ID))
                val deleteUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                cr.delete(deleteUri, null, null)
                Log.i("NYA_$TAG", "$deleteUri deleted.")
            } else {
                Log.i("NYA_$TAG", "File $uri not found.")
            }
        }?.also {
            cursor.close()
            after?.invoke()
        }
    }
    //TODO("~Why would I put this here (Part 2)? Might have to refactor later.")


    //TODO("Why would I put this here (Part 3)? Might have to refactor later.")
    fun obtainUriForNewGalleryItem(context: Context): Uri? {
        context.contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            ContentValues()
        ).also {
            Log.i("NYA_$TAG", "Obtained uri: $it")
            lastObtainedUri = it
            return it
        }
    }


    fun getAbsolutePathByUri(context: Context, uri: Uri?): String? {
        if (uri == null) {
            Log.i("NYA_$TAG", "Error getting absolute path. Uri is null.")
            return null
        }
        var pathToReturn: String? = null

        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = context.contentResolver
            .query(
                uri,
                projection,
                null,
                null,
                null
            )

        cursor?.let { safeCursor ->
            if (safeCursor.moveToFirst()) {
                pathToReturn = safeCursor.getString(safeCursor.getColumnIndex(MediaStore.Images.Media.DATA))
            }
        }

        cursor?.close()
        if (pathToReturn != null) {
            pathToReturn = Uri.fromFile(File(pathToReturn!!)).toString()
        }
        Log.i("NYA_$TAG", "Absolute path to return: $pathToReturn")
        return pathToReturn
    }


    fun getLastObtainedUri(): Uri? = lastObtainedUri
    //TODO("~Why would I put this here (Part 3)? Might have to refactor later.")


    fun prefSaveScheduler(context: Context, prefData: PrefSchedulerData) {
        with(context) {
            getSharedPreferences(
                getString(R.string.pref_filename), Context.MODE_PRIVATE
            ).edit().run {
                putString(getString(R.string.pref_sched_query_key), prefData.query)
                putInt(getString(R.string.pref_sched_pages_key), prefData.pages)
                putInt(getString(R.string.pref_sched_interval_key), prefData.interval)
                putLong(getString(R.string.pref_sched_starttime_key), prefData.startTime)
                putLong(getString(R.string.pref_sched_lasttime_key), prefData.lastTime)
                commit()
            }
        }
    }


    fun prefGetSchedulerData(context: Context): PrefSchedulerData? {
        var data: PrefSchedulerData? = null

        with(context) {
            getSharedPreferences(
                getString(R.string.pref_filename), Context.MODE_PRIVATE
            )?.let {
                val query = it.getString(getString(R.string.pref_sched_query_key), null)
                val pages = it.getInt(getString(R.string.pref_sched_pages_key), -1)
                val interval = it.getInt(getString(R.string.pref_sched_interval_key), -1)
                val startTime = it.getLong(getString(R.string.pref_sched_starttime_key), -1)
                val lastTime = it.getLong(getString(R.string.pref_sched_lasttime_key), -1)

                if (query != null &&
                        pages != -1 &&
                        interval != -1 &&
                        startTime != (-1).toLong()) {
                    data = PrefSchedulerData(
                        query,
                        pages,
                        interval,
                        startTime,
                        lastTime
                    )
                }
            }
        }

        return data
    }


    private fun prefDeleteUser(context: Context) {
        with(context) {
            getSharedPreferences(
                getString(R.string.pref_filename), Context.MODE_PRIVATE
            )?.edit()?.run {
                putString(getString(R.string.pref_username_key), null)
                putLong(getString(R.string.pref_user_id_key), -1)
                commit()
            }
        }
    }


    private fun prefSaveUser(context: Context, prefUserData: PrefUserData) {
        with(context) {
            getSharedPreferences(
                getString(R.string.pref_filename), Context.MODE_PRIVATE
            ).edit().run {
                putString(getString(R.string.pref_username_key), prefUserData.username)
                putLong(getString(R.string.pref_user_id_key), prefUserData.id)
                commit()
            }
        }
    }


    private fun prefGetUser(context: Context): PrefUserData? {
        var data: PrefUserData? = null

        with(context) {
            getSharedPreferences(
                getString(R.string.pref_filename), Context.MODE_PRIVATE
            )?.let {
                val username = it.getString(getString(R.string.pref_username_key), null)
                val id = it.getLong(getString(R.string.pref_user_id_key), -1)
                if (username != null && id != (-1).toLong()) {
                    data = PrefUserData(username, id)
                }
            }
        }

        return data
    }


    data class PrefUserData(
        val username: String,
        val id: Long
    )


    data class PrefSchedulerData(
        val query: String,
        val pages: Int,
        val interval: Int,
        val startTime: Long,
        val lastTime: Long
    )

}