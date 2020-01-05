package com.mishenka.notbasic.general

import android.app.Application
import com.mishenka.notbasic.managers.navigation.navigationModule
import com.mishenka.notbasic.managers.preservation.preservationModule
import com.mishenka.notbasic.viewmodels.eventsModule
import org.koin.core.context.startKoin

class ExtendedApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            modules(listOf(navigationModule, preservationModule, eventsModule))
        }
    }

}