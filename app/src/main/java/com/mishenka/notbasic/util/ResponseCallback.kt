package com.mishenka.notbasic.util

interface ResponseCallback {

    fun onResponseAcquired(responseMap: HashMap<String, String>)

    fun onFailure(msg: String)

}