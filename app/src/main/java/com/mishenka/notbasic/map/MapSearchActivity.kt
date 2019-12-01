package com.mishenka.notbasic.map

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.lifecycle.Observer
import com.mishenka.notbasic.R
import com.mishenka.notbasic.data.model.photo.OuterClass
import com.mishenka.notbasic.util.*
import retrofit2.Response

class MapSearchActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map_search)

        setupActionBar(R.id.map_search_tb) {
            setDisplayHomeAsUpEnabled(true)
        }

        obtainHomeVM().apply {

            mapResponseAcquired.observe(this@MapSearchActivity, Observer<Event<Pair<Response<OuterClass?>, Boolean>>> {
                it.getContentIfNotHandled()?.let { response ->
                    this.processMapSearchResult(this@MapSearchActivity, response.first, response.second)
                    Log.i("NYA", "(from MapSearchActivity) Response: ${response.first}, ${response.second}")
                }
            })

        }

        setupViewFragment()
    }


    override fun onOptionsItemSelected(item: MenuItem) =
        when(item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }


    private fun setupViewFragment() {
        supportFragmentManager.findFragmentById(R.id.map_search_content_frame)
            ?: replaceFragmentInActivity(R.id.map_search_content_frame, MapSearchFragment.newInstance())
    }

}
