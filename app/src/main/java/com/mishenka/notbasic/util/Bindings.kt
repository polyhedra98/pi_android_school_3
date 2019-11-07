package com.mishenka.notbasic.util

import android.widget.EditText
import androidx.databinding.BindingAdapter
import androidx.lifecycle.LiveData

object Bindings {

    @BindingAdapter("app:observable_error")
    @JvmStatic fun observeError(et: EditText, observable_error: LiveData<String?>) {
        if (observable_error.value.isNullOrEmpty()) {
            et.error = null
            et.clearFocus()
            et.text.clear()
        } else {
            et.error = observable_error.value
            et.selectAll()
        }
    }

}