package com.mishenka.notbasic.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.mishenka.notbasic.R
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*

@RunWith(AndroidJUnit4::class)
class DetailFragmentTest {

    private val testUrl = "https://farm66.staticflickr.com/65535/49370406682_3f458e8762.png"

    private val testCategory = "cute cat"


    @Test
    fun detailSetup_setsUpProperValues() {

        val context: Context = ApplicationProvider.getApplicationContext()
        val bundle = Bundle().apply {
            putLong(context.getString(R.string.bundle_fragment_id_key), Date().time)
            putString(context.getString(R.string.bundle_category_key), testCategory)
            putString(context.getString(R.string.bundle_url_key), testUrl)
        }
        launchFragmentInContainer<DetailFragment>(bundle, R.style.AppTheme)

        onView(withId(R.id.detail_category_tv)).check(matches(isDisplayed()))
        onView(withId(R.id.detail_category_tv)).check(matches(withText(context.getString(R.string.category_ui, testCategory))))

        onView(withId(R.id.detail_url_tv)).check(matches(isDisplayed()))
        onView(withId(R.id.detail_url_tv)).check(matches(withText(context.getString(R.string.url_ui, testUrl))))

        Thread.sleep(2000)
    }

}