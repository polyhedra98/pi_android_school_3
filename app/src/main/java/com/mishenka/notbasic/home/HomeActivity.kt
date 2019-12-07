package com.mishenka.notbasic.home

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import androidx.core.content.FileProvider
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
import com.mishenka.notbasic.gallery.GalleryFragment
import com.mishenka.notbasic.history.HistoryFragment
import com.mishenka.notbasic.map.MapFragment
import com.mishenka.notbasic.map.MapSearchActivity
import com.mishenka.notbasic.settings.SettingsActivity
import com.mishenka.notbasic.util.*
import com.mishenka.notbasic.util.Constants.JPEG_QUALITY
import com.mishenka.notbasic.util.Constants.TAKE_PHOTO_RC
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.net.URL
import java.util.*

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

            resultClicked.observe(this@HomeActivity, Observer<Event<Pair<String?, String?>>> {
                it.getContentIfNotHandled()?.let { pair ->
                    startActivity(Intent(this@HomeActivity, DetailActivity::class.java)
                        .apply {
                            putExtra(getString(R.string.intent_url_extra), pair.first)
                            putExtra(getString(R.string.intent_category_extra), pair.second)
                        })
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
                    startActivity(Intent(this@HomeActivity, MapSearchActivity::class.java))
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


    private fun takePhoto() {
        Log.i("NYA", "Taking a photo")

        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
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
                R.id.home_nav_menu_item -> conditionallyReplaceFragment(HomeFragment::class.java)
                R.id.fav_nav_menu_item -> conditionallyReplaceFragment(FavouritesFragment::class.java)
                R.id.history_nav_menu_item -> conditionallyReplaceFragment(HistoryFragment::class.java)
                R.id.map_nav_menu_item -> conditionallyReplaceFragment(MapFragment::class.java)
                R.id.gallery_nav_menu_item -> conditionallyReplaceFragment(GalleryFragment::class.java)
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
                    isAssignableFrom(GalleryFragment::class.java) ->
                        replaceFragmentInActivity(R.id.home_content_frame, GalleryFragment.newInstance())
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


    private fun saveImage(imageBitmap: Bitmap, downloaded: Boolean? = null) {
        val photoFile = try {
            createImageFile()
        } catch (e: IOException) {
            Log.i("NYA", "Exception thrown while trying to create image file")
            null
        } ?: return
        Log.i("NYA", "Saving the image")
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, FileOutputStream(photoFile))
        obtainHomeVM().insertGalleryItem(this, photoFile.toURI().toString(), downloaded)
        Log.i("NYA", "Saved successfully")
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


    private fun createImageFile(): File? {
        val filename = "not_basic_${Date().time}"
        val directory: File = getExternalFilesDir(Environment.DIRECTORY_PICTURES) ?: return null
        return File.createTempFile(filename, ".jpg", directory)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when(requestCode) {
            TAKE_PHOTO_RC -> {
                if (resultCode == Activity.RESULT_OK) {
                    val imageBitmap = data?.extras?.get("data") as Bitmap?
                    Log.i("NYA", "Received Bitmap $imageBitmap")
                    imageBitmap?.let { safeBitmap ->
                        //TODO("Add filters")
                        saveImage(safeBitmap)
                    }
                } else {
                    Log.i("NYA", "(from HomeActivity onActivityResult) Result code is not OK")
                }
            }
            else -> super.onActivityResult(requestCode, resultCode, data)

        }
    }


}
