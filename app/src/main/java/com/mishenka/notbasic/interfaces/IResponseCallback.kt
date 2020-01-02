package com.mishenka.notbasic.interfaces

interface IResponseCallback {

    fun onSuccess(data: IResponseData)

    fun onDataNotAvailable(msg: String)

}