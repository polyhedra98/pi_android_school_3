package com.mishenka.notbasic.interfaces

import android.content.Context
import androidx.fragment.app.Fragment

interface IFragmentRequest {

    val fragmentTag: String

    val navigationTitleId: Int

    val shouldBeDisplayedAlone: Boolean

    val isSecondary: Boolean

    val shouldHideToolbar: Boolean

    fun instantiateFragment(context: Context?, extras: IFragmentExtras): Fragment

}