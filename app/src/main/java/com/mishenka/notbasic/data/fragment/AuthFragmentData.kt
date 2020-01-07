package com.mishenka.notbasic.data.fragment

import com.mishenka.notbasic.interfaces.IFragmentData


data class AuthFragmentData(
    val username: String?,
    val validationError: String?
) : IFragmentData