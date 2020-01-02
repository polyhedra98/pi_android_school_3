package com.mishenka.notbasic.managers.navigation


data class RequestsStackItem(
    val primaryRequest: RequestItem,
    var children: ChildrenStack? = null
)