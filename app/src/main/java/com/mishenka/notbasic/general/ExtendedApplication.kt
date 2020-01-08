package com.mishenka.notbasic.general

import android.app.Application
import com.mishenka.notbasic.data.source.databaseModule
import com.mishenka.notbasic.managers.content.contentModule
import com.mishenka.notbasic.managers.navigation.navigationModule
import com.mishenka.notbasic.managers.preservation.preservationModule
import com.mishenka.notbasic.viewmodels.eventsModule
import com.mishenka.notbasic.viewmodels.prefsModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class ExtendedApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(applicationContext)
            modules(
                listOf(
                    navigationModule,
                    preservationModule,
                    contentModule,
                    eventsModule,
                    prefsModule,
                    databaseModule
                )
            )
        }
    }

}