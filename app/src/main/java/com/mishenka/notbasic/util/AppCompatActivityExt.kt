package com.mishenka.notbasic.util

import androidx.annotation.IdRes
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.mishenka.notbasic.home.AuthVM
import com.mishenka.notbasic.home.HomeVM
import com.mishenka.notbasic.map.LocationVM

fun AppCompatActivity.setupActionBar(@IdRes toolbarId: Int, action: ActionBar.() -> Unit) {
    setSupportActionBar(findViewById(toolbarId))
    supportActionBar?.run {
        action()
    }
}

fun AppCompatActivity.replaceFragmentInActivity(@IdRes frameId: Int, fragment: Fragment) {
    supportFragmentManager.beginTransaction().replace(frameId, fragment).commit()
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