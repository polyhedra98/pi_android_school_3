package com.mishenka.notbasic.data.fragment

import com.mishenka.notbasic.interfaces.IFragmentData


data class SchedulerFragmentData(
    val searchField: String?,
    val validationError: String?,
    val spinnerValue: Any?
) : IFragmentData