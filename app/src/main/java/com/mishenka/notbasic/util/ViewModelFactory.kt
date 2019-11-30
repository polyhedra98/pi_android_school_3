package com.mishenka.notbasic.util

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mishenka.notbasic.data.source.AppRepository
import com.mishenka.notbasic.home.AuthVM
import com.mishenka.notbasic.home.HomeVM
import com.mishenka.notbasic.map.LocationVM
import java.lang.IllegalArgumentException

class ViewModelFactory private constructor(
    private val appRepository: AppRepository
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>) =
        with(modelClass) {
            when {
                isAssignableFrom(HomeVM::class.java) ->
                    HomeVM.getInstance(appRepository)
                isAssignableFrom(AuthVM::class.java) ->
                    AuthVM.getInstance(appRepository)
                isAssignableFrom(LocationVM::class.java) ->
                    LocationVM.getInstance(appRepository)
                else -> throw IllegalArgumentException("Unknown VM class")
            }
        } as T

    companion object {

        private var INSTANCE: ViewModelFactory? = null

        fun getInstance(context: Context) =
            INSTANCE ?: synchronized(ViewModelFactory::class.java) {
                INSTANCE ?: ViewModelFactory(
                    AppRepository.getInstance(context.applicationContext)
                ).also { INSTANCE = it }
            }

    }

}