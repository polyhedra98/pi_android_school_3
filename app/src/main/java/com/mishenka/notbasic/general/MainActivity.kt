package com.mishenka.notbasic.general

import android.content.Context
import android.os.Bundle
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import com.google.android.material.navigation.NavigationView
import com.mishenka.notbasic.R
import com.mishenka.notbasic.fragments.AuthFragment
import com.mishenka.notbasic.fragments.DetailFragment
import com.mishenka.notbasic.fragments.HomeFragment
import com.mishenka.notbasic.fragments.SplashFragment
import com.mishenka.notbasic.interfaces.ISplashHost
import com.mishenka.notbasic.managers.navigation.NavigationManager
import com.mishenka.notbasic.viewmodels.EventVM
import com.mishenka.notbasic.viewmodels.PrefVM
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.ext.android.get
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class MainActivity : ExtendedActivity(), ISplashHost {

    override val mainFrameId = R.id.home_content_frame


    private val navigationManager = get<NavigationManager>()

    private val eventVM by viewModel<EventVM>()

    private val prefVM by viewModel<PrefVM>()

    private lateinit var drawerLayout: DrawerLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(null)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            setupActionBar(R.id.home_tb)
            navigationManager.run {
                conditionallyInitializeHost(this@MainActivity)
                requestInitialPopulation(SplashFragment.SplashRequest, null)
            }
        }
        else {
            mainContentRequested(false)
        }

    }


    override fun mainContentRequested(fromSplash: Boolean) {

        setupActionBar(R.id.home_tb) {
            setHomeAsUpIndicator(R.drawable.ic_menu_24px)
            setDisplayHomeAsUpEnabled(true)
        }

        setupNavigationDrawer()

        navigationManager.run {
            if (fromSplash) {
                clear()
            }
            conditionallyInitializeHost(this@MainActivity)
            requestInitialPopulation(HomeFragment.HomeRequest, null)
        }

        setupEventVM()
    }


    override fun onBackPressed() {
        navigationManager.backPressed(false)
    }


    override fun onResume() {
        super.onResume()

        navigationManager.conditionallyInitializeHost(this)
    }


    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            android.R.id.home -> {
                hideKeyboard()
                drawerLayout.openDrawer(GravityCompat.START)
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }


    override fun onPause() {
        navigationManager.removeHost()

        super.onPause()
    }


    private fun setupEventVM() {
        eventVM.apply {

            fragmentRequested.observe(this@MainActivity, Observer {
                it.getContentIfNotHandled()?.let { request ->
                    navigationManager.requestAddition(request.first, request.second)
                }
            })

            keyboardHideRequested.observe(this@MainActivity, Observer {
                it.getContentIfNotHandled()?.let {
                    hideKeyboard()
                }
            })

            detailsRequested.observe(this@MainActivity, Observer {
                it.getContentIfNotHandled()?.let { extras ->
                    navigationManager.requestAddition(DetailFragment.DetailRequest, extras)
                }
            })

            secondaryFragmentsRemovalRequested.observe(this@MainActivity, Observer {
                it.getContentIfNotHandled()?.let { tag ->
                    navigationManager.requestSecondaryFragmentsRemoval(tag)
                }
            })

            eventVM.loginCredentialsApproved.observe(this@MainActivity, Observer {
                it.getContentIfNotHandled()?.let { username ->
                    hideKeyboard()
                    eventVM.requestSecondaryFragmentsRemoval(AuthFragment.AuthRequest.fragmentTag)
                    prefVM.logIn(this@MainActivity, username)
                }
            })

            eventVM.signUpCredentialsApproved.observe(this@MainActivity, Observer {
                it.getContentIfNotHandled()?.let { username ->
                    hideKeyboard()
                    eventVM.requestSecondaryFragmentsRemoval(AuthFragment.AuthRequest.fragmentTag)
                    prefVM.signUp(this@MainActivity, username)
                }
            })

        }
    }


    private fun setupNavigationDrawer() {
        drawerLayout = home_dl
        setupDrawerContent(home_nav_view)
    }


    private fun setupDrawerContent(navigationView: NavigationView) {
        navigationView.menu.findItem(R.id.home_nav_menu_item).isChecked = true

        navigationView.setNavigationItemSelectedListener { menuItem ->
            drawerLayout.closeDrawers()

            navigationManager.navigationItemSelected(menuItem.itemId)

            true
        }
    }


    private fun hideKeyboard() {
        currentFocus?.let { safeCurrentFocus ->
            val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(safeCurrentFocus.windowToken, InputMethodManager.SHOW_FORCED)
        }
        currentFocus?.clearFocus()
    }

}
