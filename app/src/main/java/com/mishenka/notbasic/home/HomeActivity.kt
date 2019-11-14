package com.mishenka.notbasic.home

import android.content.Context
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
import com.mishenka.notbasic.favourites.FavouritesFragment
import com.mishenka.notbasic.history.HistoryFragment
import com.mishenka.notbasic.util.*
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {


    private lateinit var drawerLayout: DrawerLayout
    private var selectedItem = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        setupActionBar(R.id.home_tb) {
            setHomeAsUpIndicator(R.drawable.ic_menu_24px)
            setDisplayHomeAsUpEnabled(true)
        }

        setupViewFragment()

        setupNavigationDrawer()

        obtainVM().apply {

            queryProcessed.observe(this@HomeActivity, Observer<Event<Int>> {
                it.getContentIfNotHandled()?.let { resultCode ->
                    processValidationResult(resultCode)
                }
            })

        }
    }


    override fun onOptionsItemSelected(item: MenuItem) =
        when (item.itemId) {
            android.R.id.home -> {
                hideKeyboard()
                drawerLayout.openDrawer(GravityCompat.START)
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }


    fun obtainVM(): HomeVM =
        ViewModelProviders.of(this, ViewModelFactory.getInstance()).get(HomeVM::class.java)


    private fun setupNavigationDrawer() {
        drawerLayout = home_dl
        setupDrawerContent(home_nav_view)
    }


    private fun setupDrawerContent(navigationView: NavigationView) {
        navigationView.menu.findItem(R.id.home_nav_menu_item).isChecked = true
        selectedItem = R.id.home_nav_menu_item

        navigationView.setNavigationItemSelectedListener { menuItem ->
            navigationView.menu.findItem(selectedItem).isChecked = false

            when (menuItem.itemId) {
                R.id.home_nav_menu_item -> conditionallyReplaceFragment(HomeFragment::class.java)
                R.id.fav_nav_menu_item -> conditionallyReplaceFragment(FavouritesFragment::class.java)
                R.id.history_nav_menu_item -> conditionallyReplaceFragment(HistoryFragment::class.java)
                else -> throw IllegalStateException("Illegal menu item")
            }

            selectedItem = menuItem.itemId
            menuItem.isChecked = true
            drawerLayout.closeDrawers()
            true
        }
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