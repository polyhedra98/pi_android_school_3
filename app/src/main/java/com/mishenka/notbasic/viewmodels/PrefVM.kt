package com.mishenka.notbasic.viewmodels

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mishenka.notbasic.R
import com.mishenka.notbasic.data.model.user.History
import com.mishenka.notbasic.data.model.user.User
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
                    "userId $userId. Time: $timeStamp")
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