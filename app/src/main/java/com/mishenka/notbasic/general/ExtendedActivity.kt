package com.mishenka.notbasic.general

import androidx.annotation.IdRes
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity

abstract class ExtendedActivity : AppCompatActivity() {

    abstract val mainFrameId: Int

    protected fun setupActionBar(@IdRes toolbarId: Int, action: ActionBar.() -> Unit) {
        setSupportActionBar(findViewById(toolbarId))
        supportActionBar?.run {
            action()
        }
    }

}