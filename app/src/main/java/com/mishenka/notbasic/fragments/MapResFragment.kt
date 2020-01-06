package com.mishenka.notbasic.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mishenka.notbasic.R
import com.mishenka.notbasic.data.model.FragmentExtras
import com.mishenka.notbasic.fragments.data.MapResAdditionalExtras
import com.mishenka.notbasic.interfaces.IFragmentRequest

class MapResFragment : Fragment() {

    private val TAG = "MapResFragment"


    private var fragmentId: Long? = null

    private var lat: Double? = null

    private var lng: Double? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentId = arguments?.getLong(getString(R.string.bundle_fragment_id_key))
        lat = arguments?.getDouble(getString(R.string.bundle_lat_key))
        lng = arguments?.getDouble(getString(R.string.bundle_lng_key))

        if (fragmentId == null) {
            Log.i("NYA_$TAG", "Error. Fragment id is null.")
            throw Exception("Fragment id is null.")
        }
        if (lat == null || lng == null) {
            Log.i("NYA_$TAG", "Error. Lat / Lng is null.")
            throw Exception("Lat / Lng is null.")
        } else {
            Log.i("NYA_$TAG", "Instantiated with lat: $lat, lng: $lng")
        }
        return inflater.inflate(R.layout.fragment_map_res, container, false)
    }



    object MapResRequest : IFragmentRequest {

        override val fragmentTag: String
            get() = "MAP_RES_FRAG"

        override val navigationTitleId: Int
            get() = R.string.nav_map_res_title

        override val shouldBeDisplayedAlone: Boolean
            get() = false

        override val isSecondary: Boolean
            get() = false

        override val shouldHideToolbar: Boolean
            get() = false

        override fun instantiateFragment(context: Context, extras: FragmentExtras) = MapResFragment()
            .apply {
                arguments = Bundle().apply {
                    putLong(context.getString(R.string.bundle_fragment_id_key), extras.fragmentId)
                    putDouble(context.getString(R.string.bundle_lat_key),
                        (extras.additionalExtras as MapResAdditionalExtras).lat)
                    putDouble(context.getString(R.string.bundle_lng_key),
                        (extras.additionalExtras as MapResAdditionalExtras).lng)
                }
            }

    }
}