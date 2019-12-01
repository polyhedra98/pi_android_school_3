package com.mishenka.notbasic.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mishenka.notbasic.data.source.AppRepository

class LocationVM(
    private val appRepository: AppRepository
) : ViewModel() {

    private val _location = MutableLiveData<Pair<Double, Double>>()
    val location: LiveData<Pair<Double, Double>>
        get() = _location


    fun locationChanged(lat: Double, lng: Double) {
        _location.value = Pair(lat, lng)
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