package com.mishenka.notbasic.map

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.mishenka.notbasic.R
import com.mishenka.notbasic.util.obtainAuthVM
import com.mishenka.notbasic.util.obtainHomeVM
import com.mishenka.notbasic.util.replaceFragmentInActivity
import com.mishenka.notbasic.util.setupActionBar

class MapSearchActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map_search)

        setupViewFragment()
    }


    private fun setupViewFragment() {
        supportFragmentManager.findFragmentById(R.id.map_search_content_frame)
            ?: replaceFragmentInActivity(R.id.map_search_content_frame, MapSearchFragment.newInstance())
    }

}
