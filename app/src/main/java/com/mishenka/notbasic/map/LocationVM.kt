package com.mishenka.notbasic.map

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mishenka.notbasic.data.source.AppRepository

class LocationVM(
    private val appRepository: AppRepository
) : ViewModel() {

    private val _location = MutableLiveData<Location>()
    val location: LiveData<Location>
        get() = _location


    fun locationChanged(location: Location) {
        _location.value = location
    }


    companion object {

        private var INSTANCE: LocationVM? = null

        fun getInstance(appRepository: AppRepository) =
            INSTANCE ?: synchronized(LocationVM::class.java) {
                INSTANCE ?: LocationVM(
                    appRepository
                ).also { INSTANCE = it }
            }

    }

}