package com.mishenka.notbasic.util

import android.util.Log
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProviders
import com.mishenka.notbasic.home.AuthVM
import com.mishenka.notbasic.home.HomeVM
import com.mishenka.notbasic.map.LocationVM
import com.mishenka.notbasic.util.Constants.BACK_STACK_ROOT_TAG

fun AppCompatActivity.setupActionBar(@IdRes toolbarId: Int, action: ActionBar.() -> Unit) {
    setSupportActionBar(findViewById(toolbarId))
    supportActionBar?.run {
        action()
    }
}

fun AppCompatActivity.replaceFragmentInActivity(@IdRes frameId: Int, fragment: Fragment) {
    with(supportFragmentManager) {
        popBackStack(BACK_STACK_ROOT_TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        beginTransaction().replace(frameId, fragment)
            .addToBackStack(BACK_STACK_ROOT_TAG).commit()
        Log.i("NYA", "Current stack count: $backStackEntryCount")
    }
}


fun AppCompatActivity.addFragmentOnTop(@IdRes frameId: Int, fragment: Fragment) {
    supportFragmentManager.beginTransaction().replace(frameId, fragment)
        .addToBackStack(null).commit()
    Log.i("NYA", "Current stack count: ${supportFragmentManager.backStackEntryCount}")
}


fun AppCompatActivity.obtainHomeVM(): HomeVM =
    ViewModelProviders.of(this, ViewModelFactory.getInstance(this.applicationContext)).get(
        HomeVM::class.java)


fun AppCompatActivity.obtainAuthVM(): AuthVM =
    ViewModelProviders.of(this, ViewModelFactory.getInstance(this.applicationContext)).get(
        AuthVM::class.java)


fun AppCompatActivity.obtainLocationVM(): LocationVM =
    ViewModelProviders.of(this, ViewModelFactory.getInstance(this.applicationContext)).get(
        LocationVM::class.java
    )