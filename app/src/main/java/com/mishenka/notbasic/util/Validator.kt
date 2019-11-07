package com.mishenka.notbasic.util

object Validator {

    const val VALIDATION_RESULT_OK = 1
    const val VALIDATION_RESULT_ERROR = 2

    fun validateQuery(query: String) =
        query.matches(Regex("^[A-Za-z0-9_]*$"))

}