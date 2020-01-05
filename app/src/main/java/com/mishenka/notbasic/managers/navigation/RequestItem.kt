package com.mishenka.notbasic.managers.navigation

import com.mishenka.notbasic.data.model.FragmentExtras
import com.mishenka.notbasic.interfaces.IFragmentRequest

data class RequestItem(
    val extras: FragmentExtras,
    val request: IFragmentRequest
)