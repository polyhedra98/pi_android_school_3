package com.mishenka.notbasic.data.model

import retrofit2.Response

interface SearchCallback {

    fun onSearchCompleted(response: Response<OuterClass?>)

    fun onDataNotAvailable(message: String)

}