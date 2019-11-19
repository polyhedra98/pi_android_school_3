package com.mishenka.notbasic.util.interfaces


interface ReplaceableAdapter {

    fun <T> replaceItems(newItems: List<T>)

    fun overrideSummary(summary: String)

}