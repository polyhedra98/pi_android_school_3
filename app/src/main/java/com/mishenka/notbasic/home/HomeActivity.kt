package com.mishenka.notbasic.home

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import androidx.annotation.IdRes
import androidx.core.net.toUri
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.google.android.material.navigation.NavigationView
import com.mishenka.notbasic.R
import com.mishenka.notbasic.data.model.photo.OuterClass
import com.mishenka.notbasic.detail.DetailFragment
import com.mishenka.notbasic.favourites.FavouritesFragment
import com.mishenka.notbasic.gallery.GalleryFragment
import com.mishenka.notbasic.history.HistoryFragment
import com.mishenka.notbasic.map.MapFragment
import com.mishenka.notbasic.map.MapSearchFragment
import com.mishenka.notbasic.settings.AuthFragment
import com.mishenka.notbasic.settings.SettingsFragment
import com.mishenka.notbasic.util.*
import com.mishenka.notbasic.util.Constants.TAKE_PHOTO_RC
import com.yalantis.ucrop.UCrop
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import retrofit2.Response
import java.net.URL

class HomeActivity : AppCompatActivity() {


    private lateinit var drawerLayout: DrawerLayout

    private val systemReceiver = SystemReceiver()


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

            resultClicked.observe(this@HomeActivity, Observer<Event<Pair<String?, String?>>> {
                it.getContentIfNotHandled()?.let { pair ->
                    //TODO("For simplification, no real need to implement additional extras to pass")
                    addFragmentOnTop(R.id.home_content_frame,
                        DetailFragment.newInstance(this@HomeActivity, pair.first, pair.second))
                }
            })

            requestedTakingPhoto.observe(this@HomeActivity, Observer<Event<Unit>> {
                it.getContentIfNotHandled()?.let {
                    takePhoto()
                }
            })

            mapSearchClicked.observe(this@HomeActivity, Observer<Event<Pair<Double, Double>>> {
                it.getContentIfNotHandled()?.let { location ->
                    performMapSearch(location.first, location.second)
                    addFragmentOnTop(R.id.home_content_frame,
                        MapSearchFragment::class.java)
                }
            })

            responseAcquired.observe(this@HomeActivity, Observer<Event<Pair<Response<OuterClass?>, Boolean>>> {
                it.getContentIfNotHandled()?.let { response ->
                    this.processSearchResult(this@HomeActivity, response.first, response.second)
                }
            })

            requestedDownloadingPhoto.observe(this@HomeActivity, Observer {
                it.getContentIfNotHandled()?.let { downloadUrl ->
                    downloadImage(downloadUrl)
                }
            })

            mapResponseAcquired.observe(this@HomeActivity, Observer<Event<Pair<Response<OuterClass?>, Boolean>>> {
                it.getContentIfNotHandled()?.let { response ->
                    this.processMapSearchResult(this@HomeActivity, response.first, response.second)
                    Log.i("NYA", "(from HomeActivity) Response: ${response.first}, ${response.second}")
                }
            })

        }


        obtainAuthVM().apply {

            userLogIn.observe(this@HomeActivity, Observer<Event<Long>> {
                it.getContentIfNotHandled()?.let { safeId ->
                    obtainHomeVM().prefetchUserData(safeId)
                    hideKeyboard()
                }
            })

            userLogOut.observe(this@HomeActivity, Observer<Event<Unit>> {
                it.getContentIfNotHandled()?.let {
                    obtainHomeVM().flashData()
                }
            })

            loginRequested.observe(this@HomeActivity, Observer<Event<Unit>> {
                it.getContentIfNotHandled()?.let {
                    addFragmentOnTop(R.id.home_content_frame, AuthFragment::class.java)
                }
            })

        }

        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_BATTERY_CHANGED)
            addAction(Intent.ACTION_POWER_CONNECTED)
            addAction(Intent.ACTION_POWER_DISCONNECTED)
        }

        registerReceiver(systemReceiver, filter)

    }


    override fun onStop() {
        saveSearch()
        super.onStop()
    }


    override fun onDestroy() {
        this.unregisterReceiver(systemReceiver)
        super.onDestroy()
    }


    override fun onOptionsItemSelected(item: MenuItem) =
        when (item.itemId) {
            android.R.id.home -> {
                hideKeyboard()
                with(supportFragmentManager) {
                    if (backStackEntryCount == 1) {
                        drawerLayout.openDrawer(GravityCompat.START)
                    } else {
                        popBackStackImmediate()
                        if (backStackEntryCount == 1) {
                            supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_menu_24px)
                        }
                    }
                }
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }



    private fun takePhoto() {
        Log.i("NYA", "Taking a photo")

        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            val imageUri = obtainHomeVM().obtainUriForNewGalleryItem(this)

            takePictureIntent.resolveActivity(packageManager)?.also {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
                startActivityForResult(takePictureIntent, TAKE_PHOTO_RC)
            }
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
        //val oauthToken = obtainAuthVM().oauthToken.value
        //Log.i("NYA", "(from HomeActivity performMapSearch) oauthToken is $oauthToken")
        //if (oauthToken == null) {
        //  return
        //}
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
                R.id.home_nav_menu_item -> conditionallyReplaceFragment(R.id.home_content_frame, HomeFragment::class.java)
                R.id.fav_nav_menu_item -> conditionallyReplaceFragment(R.id.home_content_frame, FavouritesFragment::class.java)
                R.id.history_nav_menu_item -> conditionallyReplaceFragment(R.id.home_content_frame, HistoryFragment::class.java)
                R.id.map_nav_menu_item -> conditionallyReplaceFragment(R.id.home_content_frame, MapFragment::class.java)
                R.id.gallery_nav_menu_item -> conditionallyReplaceFragment(R.id.home_content_frame, GalleryFragment::class.java)
                R.id.settings_nav_menu_item -> addFragmentOnTop(R.id.home_content_frame, SettingsFragment::class.java)
                else -> throw IllegalStateException("Illegal menu item")
            }

            drawerLayout.closeDrawers()
            true
        }
    }


    private fun <A: AppCompatActivity> startActivity(activity: Class<A>) {
        startActivity(Intent(this, activity))
    }


    private fun <F: Fragment> conditionallyReplaceFragment(@IdRes contentFrame: Int, fragment: Class<F>) {
        if (fragment.isAssignableFrom(supportFragmentManager
                .findFragmentById(contentFrame)!!::class.java)) {
            Log.i("NYA", "Already in $fragment")
        } else {
            with(fragment) {
                when {
                    isAssignableFrom(HomeFragment::class.java) ->
                        replaceFragmentInActivity(contentFrame, HomeFragment.newInstance())
                    isAssignableFrom(FavouritesFragment::class.java) ->
                        replaceFragmentInActivity(contentFrame, FavouritesFragment.newInstance())
                    isAssignableFrom(HistoryFragment::class.java) ->
                        replaceFragmentInActivity(contentFrame, HistoryFragment.newInstance())
                    isAssignableFrom(MapFragment::class.java) ->
                        replaceFragmentInActivity(contentFrame, MapFragment.newInstance())
                    isAssignableFrom(MapSearchFragment::class.java) ->
                        replaceFragmentInActivity(contentFrame, MapSearchFragment.newInstance())
                    isAssignableFrom(GalleryFragment::class.java) ->
                        replaceFragmentInActivity(contentFrame, GalleryFragment.newInstance())
                    isAssignableFrom(SettingsFragment::class.java) ->
                        replaceFragmentInActivity(contentFrame, SettingsFragment.newInstance())
                    isAssignableFrom(AuthFragment::class.java) ->
                        replaceFragmentInActivity(contentFrame, AuthFragment.newInstance())
                    else ->
                        throw java.lang.IllegalStateException("Unknown fragment class")
                }
            }
        }
    }

    //TODO("This doesn't really follow SOLID principles, but I can't really change it now,
    // I would have to rework my fragments logic, which is harder than it is to write everything over")
    private fun <F: Fragment> addFragmentOnTop(@IdRes contentFrame: Int, fragment: Class<F>) {
        if (fragment.isAssignableFrom(supportFragmentManager
                .findFragmentById(contentFrame)!!::class.java)) {
            Log.i("NYA", "Already in $fragment")
        } else {
            with(fragment) {
                when {
                    isAssignableFrom(HomeFragment::class.java) ->
                        addFragmentOnTop(contentFrame, HomeFragment.newInstance())
                    isAssignableFrom(FavouritesFragment::class.java) ->
                        addFragmentOnTop(contentFrame, FavouritesFragment.newInstance())
                    isAssignableFrom(HistoryFragment::class.java) ->
                        addFragmentOnTop(contentFrame, HistoryFragment.newInstance())
                    isAssignableFrom(MapFragment::class.java) ->
                        addFragmentOnTop(contentFrame, MapFragment.newInstance())
                    isAssignableFrom(MapSearchFragment::class.java) ->
                        addFragmentOnTop(contentFrame, MapSearchFragment.newInstance())
                    isAssignableFrom(GalleryFragment::class.java) ->
                        addFragmentOnTop(contentFrame, GalleryFragment.newInstance())
                    isAssignableFrom(SettingsFragment::class.java) ->
                        addFragmentOnTop(contentFrame, SettingsFragment.newInstance())
                    isAssignableFrom(AuthFragment::class.java) ->
                        addFragmentOnTop(contentFrame, AuthFragment.newInstance())
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
        currentFocus?.clearFocus()
    }


    private fun saveImage(imageBitmap: Bitmap, downloaded: Boolean? = null) {
        obtainHomeVM().saveGalleryItem(this, imageBitmap, downloaded)
    }


    //TODO(Old java way of downloading)
    private fun downloadImage(urlString: String) {
        //TODO(Change scope)
        GlobalScope.launch {
            val url = URL(urlString)
            val connection = url.openConnection()
            connection.doInput = true
            connection.connect()
            val inputStream = connection.getInputStream()
            val downloadedBitmap = BitmapFactory.decodeStream(inputStream)
            MainScope().launch {
                saveImage(downloadedBitmap, true)
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when(requestCode) {
            TAKE_PHOTO_RC -> {
                if (resultCode == Activity.RESULT_OK) {
                    obtainHomeVM().getLastObtainedUri()?.let { safeUri ->
                        UCrop.of(safeUri.toUri(), safeUri.toUri()).start(this)
                    }
                } else {
                    Log.i("NYA", "(from HomeActivity onActivityResult) Result code is not OK (from camera)")
                }
            }
            UCrop.REQUEST_CROP -> {
                if (resultCode == Activity.RESULT_OK) {
                    data?.let {
                        val uri = UCrop.getOutput(it)
                        uri?.let { safeUri ->
                            obtainHomeVM().insertGalleryItem(this, safeUri.toString())
                        }
                    }
                } else {
                    Log.i("NYA", "(from HomeActivity onActivityResult) Result code is not OK (from UCrop) ${UCrop.getError(data!!)}")
                }
            }
            else -> super.onActivityResult(requestCode, resultCode, data)

        }
    }


}
