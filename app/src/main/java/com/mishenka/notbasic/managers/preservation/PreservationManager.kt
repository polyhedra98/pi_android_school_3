package com.mishenka.notbasic.managers.preservation

import android.util.Log
import com.mishenka.notbasic.interfaces.IFragmentData
import org.koin.dsl.module

val preservationModule = module {
    single { PreservationManager() }
}


//TODO("Clean storage from removed fragments data.")
class PreservationManager {

    private val TAG = "PreservationManager"


    private val dataStorage = mutableMapOf<Long, IFragmentData>()


    fun preserveFragmentData(fragmentId: Long, fragmentData: IFragmentData) {
        Log.i("NYA_$TAG", "Preserving data for fragment #$fragmentId")
        dataStorage[fragmentId] = fragmentData
    }

    fun getDataForFragment(fragmentId: Long): IFragmentData? {
        Log.i("NYA_$TAG", "Getting data for fragment #$fragmentId")
        return dataStorage[fragmentId]
    }


    fun clearDataForFragment(fragmentId: Long) {
        Log.i("NYA_$TAG", "Clearing data for fragment #$fragmentId")
        dataStorage.remove(fragmentId)
    }

}