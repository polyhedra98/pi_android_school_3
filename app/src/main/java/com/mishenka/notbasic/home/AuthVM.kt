package com.mishenka.notbasic.home

import android.content.Context
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mishenka.notbasic.R
import com.mishenka.notbasic.data.model.user.User
import com.mishenka.notbasic.data.source.AppRepository
import com.mishenka.notbasic.settings.AuthCallback
import com.mishenka.notbasic.util.Event
import com.mishenka.notbasic.util.Validator
import kotlinx.coroutines.launch

class AuthVM private constructor(
    private val appRepository: AppRepository
) : ViewModel() {


    private val _username = MutableLiveData<String?>().apply { value = null }
    val username: LiveData<String?>
        get() = _username

    private val _loginError = MutableLiveData<Event<String?>>().apply { value = Event(null) }
    val loginError: LiveData<Event<String?>>
        get() = _loginError


    fun start(context: Context) {
        getSavedUser(context)
    }


    fun createUser(username: String, context: Context, callback: AuthCallback? = null) {
        if (!Validator.validateUsername(username)) {
            _loginError.value = Event(context.getString(R.string.username_validation_error))
            return
        }
        viewModelScope.launch {
            if (appRepository.getUserIdByUsername(username) == null) {
                appRepository.insertUser(User(0, username))
                saveUser(username, context)
                callback?.onAuthenticationFinished()
            } else {
                _loginError.value = Event(context.getString(R.string.username_existence_collision))
            }
        }
    }


    fun logInUser(username: String, context: Context, callback: AuthCallback? = null) {
        if (!Validator.validateUsername(username)) {
            _loginError.value = Event(context.getString(R.string.username_validation_error))
            return
        }
        viewModelScope.launch {
            if (appRepository.getUserIdByUsername(username) == null) {
                _loginError.value = Event(context.getString(R.string.username_existence_error))
            } else {
                _loginError.value = Event(null)
                saveUser(username, context)
                callback?.onAuthenticationFinished()
            }
        }
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


    private fun saveUser(username: String, context: Context) {
        _username.value = username
        with(context) {
            getSharedPreferences(
                getString(R.string.preferences_filename), Context.MODE_PRIVATE
            )?.let { safePreferences ->
                safePreferences.edit()
                    .putString(getString(R.string.preferences_username), username)
                    .commit()
            }
        }
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