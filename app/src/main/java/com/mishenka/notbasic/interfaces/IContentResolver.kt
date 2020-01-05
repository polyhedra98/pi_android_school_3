package com.mishenka.notbasic.interfaces

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.mishenka.notbasic.managers.content.Response

interface IContentResolver {

    val conventionalName: String


    fun fetchData(context: Context,
                  dataRequest: IRequestData,
                  fragmentData: IFragmentData?,
                  observableToUpdate: MutableLiveData<Response>)

}