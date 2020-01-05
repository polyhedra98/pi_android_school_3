package com.mishenka.notbasic.interfaces

import androidx.lifecycle.MutableLiveData

interface IContentResolver {

    fun fetchData(observable: MutableLiveData<IContentResponse>, extras: IContentExtras)

}