package com.mishenka.notbasic.interfaces

import androidx.fragment.app.Fragment

interface IFragmentRequest {

    val fragmentTag: String

    val navigationTitleId: Int

    val shouldBeDisplayedAlone: Boolean

    val isSecondary: Boolean

    val shouldHideToolbar: Boolean

    fun instantiateFragment(extras: IFragmentExtras?): Fragment

}