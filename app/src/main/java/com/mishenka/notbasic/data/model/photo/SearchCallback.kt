package com.mishenka.notbasic.data.model.photo

import retrofit2.Response

interface SearchCallback {

    fun onSearchCompleted(response: Response<OuterClass?>)

    fun onDataNotAvailable(message: String)

}