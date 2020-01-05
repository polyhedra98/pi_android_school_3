package com.mishenka.notbasic.managers.content

import com.mishenka.notbasic.data.ErrorTypes
import com.mishenka.notbasic.interfaces.IResponseData


//TODO("Would be nice to return one type, instead of initializing both..")
data class Response(
    val error: Pair<ErrorTypes, String>?,
    val data: IResponseData?
)