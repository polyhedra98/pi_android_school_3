package com.mishenka.notbasic.managers.content

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mishenka.notbasic.data.DataTypes
import com.mishenka.notbasic.interfaces.IContentResolver
import com.mishenka.notbasic.interfaces.IFragmentData
import com.mishenka.notbasic.interfaces.IRequestData
import com.mishenka.notbasic.interfaces.IResponseData
import org.koin.dsl.module


val contentModule = module {
    single { ContentManager() }
}


class ContentManager {

    private val TAG = "ContentManager"


    private val contentResolvers = mapOf<DataTypes, IContentResolver>(
        Pair(DataTypes.STD_SEARCH, ContentResolverStd())
    )


    private val responseStorage = mutableMapOf<Long, MutableLiveData<IResponseData?>>()

    private val dataStorage = mutableMapOf<Long, IFragmentData>()



    fun registerFragment(fragmentId: Long, fragmentData: IFragmentData) {
        dataStorage[fragmentId] = fragmentData
    }


    fun updateFragmentData(fragmentId: Long, fragmentData: IFragmentData) {
        dataStorage[fragmentId] = fragmentData
    }


    fun getFragmentData(fragmentId: Long): IFragmentData? =
        dataStorage[fragmentId]


    fun requestContent(dataRequest: IRequestData) {
        Log.i("NYA_$TAG", "Data requested for fragment #${dataRequest.fragmentId}")

        Log.i("NYA_$TAG", "Data fetching assigned to " +
                "${contentResolvers[dataRequest.ofType]?.conventionalName}")
        if (responseStorage[dataRequest.fragmentId] == null) {
            getObservableForFragment(dataRequest.fragmentId)
        }
        contentResolvers[dataRequest.ofType]?.fetchData(dataRequest,
            dataStorage[dataRequest.fragmentId], responseStorage[dataRequest.fragmentId]!!)
    }


    fun getObservableForFragment(fragmentId: Long): LiveData<IResponseData?> {
        return if (responseStorage[fragmentId] != null) {
            responseStorage[fragmentId]!!
        } else {
            val observable = MutableLiveData<IResponseData?>()
            responseStorage[fragmentId] = observable
            responseStorage[fragmentId]!!
        }
    }

}