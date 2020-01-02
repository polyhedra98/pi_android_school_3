package com.mishenka.notbasic.managers.content

import android.util.Log
import com.mishenka.notbasic.data.DataTypes
import com.mishenka.notbasic.interfaces.IContentResolver
import com.mishenka.notbasic.interfaces.IRequestData
import com.mishenka.notbasic.interfaces.IResponseCallback
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


    private val responseStorage = mutableMapOf<Long, IResponseData?>()


    fun requestData(dataRequest: IRequestData) {
        Log.i("NYA_$TAG", "Data requested for fragment #${dataRequest.fragmentId}")

        Log.i("NYA_$TAG", "Data fetching assigned to " +
                "${contentResolvers[dataRequest.ofType]?.conventionalName}")
        contentResolvers[dataRequest.ofType]?.fetchData(dataRequest, object : IResponseCallback {

            override fun onSuccess(data: IResponseData) {
                responseStorage[dataRequest.fragmentId] = data
                dataRequest.callback.onSuccess(data)
            }

            override fun onDataNotAvailable(msg: String) {
                responseStorage[dataRequest.fragmentId] = null
                dataRequest.callback.onDataNotAvailable(msg)
            }
        })
    }

}