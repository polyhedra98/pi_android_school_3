package com.mishenka.notbasic.map

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.mishenka.notbasic.R
import kotlinx.android.synthetic.main.activity_map_search.*

class MapSearchActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map_search)

        var location: String? = null
        intent?.extras?.let { safeExtras ->
            location = safeExtras.getString(getString(R.string.intent_location_extra))
        }

        setupLocationRelatedViews(location)
    }


    private fun setupLocationRelatedViews(location: String?) {
        map_temp_tv.text = location ?: getString(R.string.empty_location_error)
    }

}
