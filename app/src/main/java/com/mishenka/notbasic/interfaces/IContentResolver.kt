package com.mishenka.notbasic.interfaces

interface IContentResolver {

    val conventionalName: String


    fun fetchData(dataRequest: IRequestData, callback: IResponseCallback)

}