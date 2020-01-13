package com.mishenka.notbasic.viewmodels

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.mishenka.notbasic.data.source.AppDatabase
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock


@RunWith(AndroidJUnit4::class)
class PrefVMTest {

    private lateinit var prefVM: PrefVM


    @Before
    fun setupViewModel() {
        prefVM = PrefVM(mock(AppDatabase::class.java))
    }


    @Test
    fun setupPowerPreference_setsPowerNotificationPreferred() {

        prefVM.setupPowerPreference(ApplicationProvider.getApplicationContext())

        val powerPrefValue = prefVM.powerNotificationsPreferred.value

        assert(
            powerPrefValue != null
        )
    }

}