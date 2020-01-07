package com.mishenka.notbasic.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


val prefsModule = module {
    viewModel { PrefVM() }
}


class PrefVM: ViewModel() {

    private val _username = MutableLiveData<String?>().apply { value = null }
    val username: LiveData<String?>
        get() = _username


    //TODO("Implement.")
    fun logOut() {
        _username.value = null
    }

}