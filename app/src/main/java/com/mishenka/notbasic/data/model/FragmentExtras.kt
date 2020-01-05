package com.mishenka.notbasic.data.model

import com.mishenka.notbasic.interfaces.IFragmentAdditionalExtras

data class FragmentExtras (
    val fragmentId: Long,
    val additionalExtras: IFragmentAdditionalExtras?
)