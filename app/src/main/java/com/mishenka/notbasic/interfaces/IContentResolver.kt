package com.mishenka.notbasic.interfaces

import androidx.lifecycle.MutableLiveData

interface IContentResolver {

    val conventionalName: String


    fun fetchData(dataRequest: IRequestData,
                  fragmentData: IFragmentData?,
                  observableToUpdate: MutableLiveData<IResponseData?>)

}