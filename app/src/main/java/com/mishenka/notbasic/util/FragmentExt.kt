package com.mishenka.notbasic.util

import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.mishenka.notbasic.R

fun Fragment.setupToolbar(@StringRes titleRes: Int, shouldChangeToBack: Boolean = false) {
    (activity as AppCompatActivity).supportActionBar?.let { toolbar ->
        with(toolbar) {
            setTitle(titleRes)
            if (shouldChangeToBack) {
                setHomeAsUpIndicator(R.drawable.ic_arrow_back_24px)
            }
        }
    }
}