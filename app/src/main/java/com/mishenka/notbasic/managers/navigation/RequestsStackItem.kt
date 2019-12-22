package com.mishenka.notbasic.managers.navigation

import com.mishenka.notbasic.interfaces.IFragmentRequest

data class RequestsStackItem(
    val primaryRequest: IFragmentRequest,
    var children: ChildrenStack? = null
)