package com.mishenka.notbasic.managers.navigation


data class RequestsStackItem(
    val primaryItem: RequestItem,
    var children: ChildrenStack? = null
)