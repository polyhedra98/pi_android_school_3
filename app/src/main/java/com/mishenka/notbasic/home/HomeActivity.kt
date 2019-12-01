package com.mishenka.notbasic.home

import android.content.Context
import android.content.Intent
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.navigation.NavigationView
import com.mishenka.notbasic.R
import com.mishenka.notbasic.data.model.photo.OuterClass
import com.mishenka.notbasic.detail.DetailActivity
import com.mishenka.notbasic.favourites.FavouritesFragment
import com.mishenka.notbasic.history.HistoryFragment
import com.mishenka.notbasic.map.MapFragment
import com.mishenka.notbasic.map.MapSearchActivity
import com.mishenka.notbasic.settings.SettingsActivity
import com.mishenka.notbasic.util.*
import kotlinx.android.synthetic.main.activity_home.*
import retrofit2.Response

class HomeActivity : AppCompatActivity() {


    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        setupActionBar(R.id.home_tb) {
            setHomeAsUpIndicator(R.drawable.ic_menu_24px)
            setDisplayHomeAsUpEnabled(true)
        }

        setupViewFragment()

        setupNavigationDrawer()

        obtainHomeVM().apply {

            searchField.value = loadSearch()

            queryProcessed.observe(this@HomeActivity, Observer<Event<Int>> {
                it.getContentIfNotHandled()?.let { resultCode ->
                    processValidationResult(resultCode)
                }
            })

            resultClicked.observe(this@HomeActivity, Observer<Event<Pair<String, String>>> {
                it.getContentIfNotHandled()?.let { pair ->
                    startActivity(Intent(this@HomeActivity, DetailActivity::class.java)
                        .apply {
                            putExtra(getString(R.string.intent_url_extra), pair.first)
                            putExtra(getString(R.string.intent_category_extra), pair.second)
                        })
                }
            })

            mapSearchClicked.observe(this@HomeActivity, Observer<Event<Pair<Double, Double>>> {
                it.getContentIfNotHandled()?.let { location ->
                    performMapSearch(location.first, location.second)
                    startActivity(Intent(this@HomeActivity, MapSearchActivity::class.java))
                }
            })

            responseAcquired.observe(this@HomeActivity, Observer<Event<Pair<Response<OuterClass?>, Boolean>>> {
                it.getContentIfNotHandled()?.let { response ->
                    this.processSearchResult(this@HomeActivity, response.first, response.second)
                }
            })
        }
    }


    override fun onStop() {
        saveSearch()
        super.onStop()
    }


    override fun onOptionsItemSelected(item: MenuItem) =
        when (item.itemId) {
            android.R.id.home -> {
                hideKeyboard()
                currentFocus?.clearFocus()
                drawerLayout.openDrawer(GravityCompat.START)
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }


    private fun loadSearch(): String? {
        var previousSearch: String? = null
        getSharedPreferences(
            getString(R.string.preferences_filename), Context.MODE_PRIVATE
        )?.let { safePreferences ->
            previousSearch = safePreferences.getString(getString(R.string.preferences_search), null)
        }
        return previousSearch
    }


    private fun performMapSearch(lat: Double, lng: Double) {
        obtainHomeVM().mapSearch(this, lat.toString(),
            lng.toString(), obtainAuthVM().userId.value)
    }


    private fun saveSearch() {
        getSharedPreferences(
            getString(R.string.preferences_filename), Context.MODE_PRIVATE
        )?.let { safePreferences ->
            val searchField = obtainHomeVM().searchField.value
            safePreferences.edit()
                .putString(getString(R.string.preferences_search), searchField)
                .apply()
        }
    }


    private fun setupNavigationDrawer() {
        drawerLayout = home_dl
        setupDrawerContent(home_nav_view)
    }


    private fun setupDrawerContent(navigationView: NavigationView) {
        navigationView.menu.findItem(R.id.home_nav_menu_item).isChecked = true

        navigationView.setNavigationItemSelectedListener { menuItem ->

            when (menuItem.itemId) {
                R.id.home_nav_menu_item -> conditionallyReplaceFragment(HomeFragment::class.java)
                R.id.fav_nav_menu_item -> conditionallyReplaceFragment(FavouritesFragment::class.java)
                R.id.history_nav_menu_item -> conditionallyReplaceFragment(HistoryFragment::class.java)
                R.id.map_nav_menu_item -> conditionallyReplaceFragment(MapFragment::class.java)
                R.id.settings_nav_menu_item -> startActivity(SettingsActivity::class.java)
                else -> throw IllegalStateException("Illegal menu item")
            }

            drawerLayout.closeDrawers()
            true
        }
    }


    private fun <A: AppCompatActivity> startActivity(activity: Class<A>) {
        startActivity(Intent(this, activity))
    }


    private fun <F: Fragment> conditionallyReplaceFragment(fragment: Class<F>) {
        if (fragment.isAssignableFrom(supportFragmentManager
                .findFragmentById(R.id.home_content_frame)!!::class.java)) {
            Log.i("NYA", "Already in $fragment")
        } else {
            with(fragment) {
                when {
                    isAssignableFrom(HomeFragment::class.java) ->
                        replaceFragmentInActivity(R.id.home_content_frame, HomeFragment.newInstance())
                    isAssignableFrom(FavouritesFragment::class.java) ->
                        replaceFragmentInActivity(R.id.home_content_frame, FavouritesFragment.newInstance())
                    isAssignableFrom(HistoryFragment::class.java) ->
                        replaceFragmentInActivity(R.id.home_content_frame, HistoryFragment.newInstance())
                    isAssignableFrom(MapFragment::class.java) ->
                        replaceFragmentInActivity(R.id.home_content_frame, MapFragment.newInstance())
                    else ->
                        throw java.lang.IllegalStateException("Unknown fragment class")
                }
            }
        }
    }


    private fun setupViewFragment() {
        supportFragmentManager.findFragmentById(R.id.home_content_frame)
            ?: replaceFragmentInActivity(R.id.home_content_frame, HomeFragment.newInstance())
    }


    private fun processValidationResult(resultCode: Int) {
        when(resultCode) {
            Validator.VALIDATION_RESULT_OK -> {
                Log.i("NYA", "Successful query validation")
                hideKeyboard()
            }
            Validator.VALIDATION_RESULT_ERROR -> {
                Log.i("NYA","Query validation error")
            }
            else -> throw IllegalStateException("Illegal result code")
        }
    }


    private fun hideKeyboard() {
        currentFocus?.let { safeCurrentFocus ->
            val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(safeCurrentFocus.windowToken, InputMethodManager.SHOW_FORCED)
        }
    }

}
