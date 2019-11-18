package com.mishenka.notbasic.home

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mishenka.notbasic.R
import com.mishenka.notbasic.data.source.AppRepository

class AuthVM private constructor(
    private val appRepository: AppRepository
) : ViewModel() {


    private val _username = MutableLiveData<String?>().apply { value = null }
    val username: LiveData<String?>
        get() = _username


    fun start(context: Context) {
        getSavedUser(context)
    }


    fun logInUser(username: String, context: Context) {

    }


    fun logOutUser(context: Context) {
        with(context) {
            getSharedPreferences(
                getString(R.string.preferences_filename), Context.MODE_PRIVATE
            )?.let { safePreferences ->
                _username.value = null
                safePreferences.edit()
                    .putString(getString(R.string.preferences_username), null)
                    .commit()
            }
        }
    }



    private fun getSavedUser(context: Context) {
        var username: String? = null
        with(context) {
            getSharedPreferences(
                getString(R.string.preferences_filename), Context.MODE_PRIVATE
            )?.let { safePreferences ->
                username = safePreferences.getString(
                    getString(R.string.preferences_username),
                    null
                )
            }
        }
        _username.value = username
    }


    companion object {

        private var INSTANCE: AuthVM? = null

        fun getInstance(appRepository: AppRepository) =
            INSTANCE ?: synchronized(AuthVM::class.java) {
                INSTANCE ?: AuthVM(
                    appRepository
                ).also { INSTANCE = it }
            }

    }

}