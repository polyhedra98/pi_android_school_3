package com.mishenka.notbasic.viewmodels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mishenka.notbasic.R
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


val prefsModule = module {
    viewModel { PrefVM() }
}


class PrefVM: ViewModel() {

    private val _username = MutableLiveData<String?>().apply { value = null }
    val username: LiveData<String?>
        get() = _username



    fun start(context: Context) {
        _username.value = prefGetUser(context)
    }


    //TODO("Implement.")
    fun logIn(context: Context, username: String) {
        _username.value = username
        prefSaveUser(context, username)
    }


    //TODO("Implement.")
    fun logOut(context: Context) {
        _username.value = null
        prefDeleteUser(context)
    }


    private fun prefDeleteUser(context: Context) {
        with(context) {
            getSharedPreferences(
                getString(R.string.pref_filename), Context.MODE_PRIVATE
            )?.edit()?.run {
                putString(getString(R.string.pref_username_key), null)
                commit()
            }
        }
    }


    private fun prefSaveUser(context: Context, username: String) {
        with(context) {
            getSharedPreferences(
                getString(R.string.pref_filename), Context.MODE_PRIVATE
            ).edit().run {
                putString(getString(R.string.pref_username_key), username)
                commit()
            }
        }
    }


    private fun prefGetUser(context: Context): String? {
        var username: String? = null

        with(context) {
            getSharedPreferences(
                getString(R.string.pref_filename), Context.MODE_PRIVATE
            )?.let {
                username = it.getString(getString(R.string.pref_username_key), null)
            }
        }

        return username
    }

}