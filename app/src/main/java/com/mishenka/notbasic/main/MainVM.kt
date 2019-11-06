package com.mishenka.notbasic.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mishenka.notbasic.data.source.AppRepository

class MainVM private constructor(
    private val appRepository: AppRepository
) : ViewModel() {

    private val _helloWorld = MutableLiveData<String>()
    val helloWorld: LiveData<String>
        get() = _helloWorld


    fun setHelloWorld(hello: String?) {
        _helloWorld.value = hello.toString()
    }

    companion object {

        private var INSTANCE: MainVM? = null

        fun getInstance(appRepository: AppRepository) =
            INSTANCE ?: synchronized(MainVM::class.java) {
                INSTANCE ?: MainVM(
                    appRepository
                ).also { INSTANCE = it }
            }

    }

}