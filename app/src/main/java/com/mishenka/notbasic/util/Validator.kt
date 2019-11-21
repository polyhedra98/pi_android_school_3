package com.mishenka.notbasic.util

object Validator {

    const val VALIDATION_RESULT_OK = 1
    const val VALIDATION_RESULT_ERROR = 2

    fun validateQuery(query: String) =
        (query.matches(Regex("^[A-Za-z0-9_]*$")) && query.isNotBlank())

    fun validateUsername(username: String) =
        (username.matches(Regex("^[A-Za-z0-9_]*$")) && username.isNotBlank())

    fun validateSearchAndUrl(currentSearch: String?, currentUrl: String?) =
        currentUrl != null && currentSearch != null

    fun validateFavAndCategoryId(currentFavId: Long, currentCategoryId: Long) =
        currentFavId != (-1).toLong() && currentCategoryId != (-1).toLong()

}