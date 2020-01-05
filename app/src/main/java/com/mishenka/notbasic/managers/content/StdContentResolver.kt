package com.mishenka.notbasic.managers.content

import androidx.lifecycle.MutableLiveData
import com.mishenka.notbasic.data.content.StdContentResponse
import com.mishenka.notbasic.interfaces.IContentExtras
import com.mishenka.notbasic.interfaces.IContentResolver
import com.mishenka.notbasic.interfaces.IContentResponse

class StdContentResolver : IContentResolver {

    override fun fetchData(observable: MutableLiveData<IContentResponse>, extras: IContentExtras) {
        val fakeData = listOf("FAKE_DATA", "FAKE_DATA", "FAKE_DATA")
        observable.value = StdContentResponse(fakeData)
    }

}