package com.mishenka.notbasic.managers.content

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mishenka.notbasic.data.content.ContentType
import com.mishenka.notbasic.interfaces.IContentExtras
import com.mishenka.notbasic.interfaces.IContentResolver
import com.mishenka.notbasic.interfaces.IContentResponse
import org.koin.dsl.module

val contentModule = module {
    single { ContentManager() }
}


class ContentManager {

    private val TAG = "ContentManager"


    private val resolversPool = mutableMapOf<ContentType, IContentResolver>(
        Pair(ContentType.STD_TYPE, StdContentResolver()),
        Pair(ContentType.LAT_LNG_TYPE, LatLngContentResolver())
    )


    fun requestContent(ofType: ContentType, extras: IContentExtras): LiveData<IContentResponse> {
        val resolver = resolversPool[ofType]
        if (resolver == null) {
            Log.i("NYA_$TAG", "Error. Unsupported content type.")
            throw Exception("Unsupported content type.")
        }

        val observable = MutableLiveData<IContentResponse>()
        resolver.fetchData(observable, extras)
        return observable
    }

}