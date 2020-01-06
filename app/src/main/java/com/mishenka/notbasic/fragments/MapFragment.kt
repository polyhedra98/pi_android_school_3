package com.mishenka.notbasic.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.mishenka.notbasic.R
import com.mishenka.notbasic.data.model.FragmentExtras
import com.mishenka.notbasic.fragments.data.MapFragmentData
import com.mishenka.notbasic.fragments.data.MapResAdditionalExtras
import com.mishenka.notbasic.interfaces.IFragmentRequest
import com.mishenka.notbasic.managers.preservation.PreservationManager
import com.mishenka.notbasic.viewmodels.EventVM
import kotlinx.android.synthetic.main.fragment_map.*
import org.koin.android.ext.android.get
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


class MapFragment : Fragment(), OnMapReadyCallback {

    private val TAG = "MapFragment"


    private val eventVM by sharedViewModel<EventVM>()

    private val preservationManager = get<PreservationManager>()


    private var fragmentId: Long? = null

    private var restoredData: MapFragmentData? = null

    private var map: GoogleMap? = null

    private var lat: Double? = null

    private var lng: Double? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentId = arguments?.getLong(getString(R.string.bundle_fragment_id_key))
        if (fragmentId == null) {
            Log.i("NYA_$TAG", "Error. Fragment id is null.")
            throw Exception("Fragment id is null.")
        }
        return inflater.inflate(R.layout.fragment_map, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        restoredData = (preservationManager.getDataForFragment(fragmentId!!) as? MapFragmentData?)

        lat = restoredData?.lat
        lng = restoredData?.lng

        if (lat != null && lng != null) {
            updateLatLng(lat!!, lng!!)
        }

        setupViews()
    }


    override fun onDestroyView() {

        preservationManager.preserveFragmentData(fragmentId!!, MapFragmentData(
            lat = lat,
            lng = lng
        ))

        super.onDestroyView()
    }


    override fun onMapReady(googleMap: GoogleMap?) {
        Log.i("NYA_$TAG", "Map is ready. Is null? ${googleMap == null}")
        map = googleMap

        if (lat != null && lng != null) {
            placeMarker(lat!!, lng!!)
            centerCamera(lat!!, lng!!)
        }

        map?.setOnMapLongClickListener {
            updateLatLng(it.latitude, it.longitude)
            placeMarker(it.latitude, it.longitude)
        }
        map?.setOnMarkerDragListener(object : GoogleMap.OnMarkerDragListener {

            override fun onMarkerDragEnd(p0: Marker?) {
                Log.i("NYA_$TAG", "Marker drag end.")
                p0?.position?.let { position ->
                    updateLatLng(position.latitude, position.longitude)
                }
            }

            override fun onMarkerDragStart(p0: Marker?) {
                Log.i("NYA_$TAG", "Marker drag start.")
            }

            override fun onMarkerDrag(p0: Marker?) {
                Log.i("NYA_$TAG", "Marker drag.")
                p0?.position?.let { position ->
                    updateLatLng(position.latitude, position.longitude)
                }
            }

        })

        //TODO("Track location in the future.")

    }


    private fun setupViews() {
        setupMap()

        map_center_b.setOnClickListener {
            centerCamera(lat!!, lng!!)
        }
        map_search_b.setOnClickListener {
            eventVM.requestFragment(
                MapResFragment.MapResRequest,
                MapResAdditionalExtras(
                    lat!!,
                    lng!!
                )
            )
        }
    }


    private fun setupMap() {
        (childFragmentManager.findFragmentById(R.id.map_f) as SupportMapFragment?)?.
            getMapAsync(this)
    }


    private fun updateLatLng(lat: Double, lng: Double) {
        this.lat = lat
        this.lng = lng

        activateLocationRelatedViews()

        map_location_tv.text = getString(R.string.location_ui, lat, lng)
    }


    private fun activateLocationRelatedViews() {
        if (!map_center_b.isEnabled) {
            map_center_b.isEnabled = true
        }
        if (!map_search_b.isEnabled) {
            map_search_b.isEnabled = true
        }
    }


    private fun placeMarker(lat: Double, lng: Double) {
        removeAllMarkers()
        map?.addMarker(MarkerOptions().position(LatLng(lat, lng))
            .title(getString(R.string.current_location_ui))
            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
            .draggable(true)
        )
    }


    private fun removeAllMarkers() {
        map?.clear()
    }


    private fun centerCamera(lat: Double, lng: Double) {
        map?.moveCamera(CameraUpdateFactory.newLatLng(LatLng(lat, lng)))
    }



    object MapRequest : IFragmentRequest {

        override val fragmentTag: String
            get() = "MAP_FRAG"

        override val navigationTitleId: Int
            get() = R.string.nav_map_title

        override val shouldBeDisplayedAlone: Boolean
            get() = true

        override val isSecondary: Boolean
            get() = false

        override val shouldHideToolbar: Boolean
            get() = false

        override fun instantiateFragment(context: Context, extras: FragmentExtras) = MapFragment()
            .apply {
                arguments = Bundle().apply {
                    putLong(context.getString(R.string.bundle_fragment_id_key), extras.fragmentId)
                }
            }

    }


}