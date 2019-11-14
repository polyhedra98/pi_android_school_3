package com.mishenka.notbasic.util

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mishenka.notbasic.data.source.AppRepository
import com.mishenka.notbasic.home.HomeVM
import java.lang.IllegalArgumentException

class ViewModelFactory private constructor(
    private val appRepository: AppRepository
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>) =
        with(modelClass) {
            when {
                isAssignableFrom(HomeVM::class.java) ->
                    HomeVM.getInstance(appRepository)
                else -> throw IllegalArgumentException("Unknown VM class")
            }
        } as T

    companion object {

        private var INSTANCE: ViewModelFactory? = null

        fun getInstance() =
            INSTANCE ?: synchronized(ViewModelFactory::class.java) {
                INSTANCE ?: ViewModelFactory(
                    AppRepository.getInstance()
                ).also { INSTANCE = it }
            }

    }

}