package com.mishenka.notbasic.viewmodels

import android.content.Context
import android.util.Log
import android.widget.Button
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mishenka.notbasic.R
import com.mishenka.notbasic.data.model.user.*
import com.mishenka.notbasic.data.source.AppDatabase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
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



    fun start(context: Context) {
        val prefUserData = prefGetUser(context)
        _username.value = prefUserData?.username
        _userId.value = prefUserData?.id
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

}