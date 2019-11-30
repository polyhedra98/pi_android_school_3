package com.mishenka.notbasic.map

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.google.android.gms.location.LocationServices
import com.mishenka.notbasic.R
import com.mishenka.notbasic.databinding.FragmentMapBinding
import com.mishenka.notbasic.util.obtainHomeVM
import com.mishenka.notbasic.util.obtainLocationVM

class MapFragment : Fragment() {

    private lateinit var binding: FragmentMapBinding
    private val LOCATION_PERMISSION_REQUEST_CODE = 1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        (DataBindingUtil.inflate(inflater, R.layout.fragment_map, container, false)
                as FragmentMapBinding)
            .apply {
                locVM = (activity as AppCompatActivity).obtainLocationVM()

                lifecycleOwner = activity
            }.also { binding = it }.root


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupLocation()
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Log.i("NYA", "Permission has been denied")
                } else {
                    Log.i("NYA", "Permission has been accepted")
                    setupLocation()
                }
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }


    private fun setupLocation() {
        if (!getFineLocationPermission()) {
            Log.i("NYA", "Permission denied")
            requestPermission()
        } else {
            setupFusedLocationClient()
            setupBindings()
        }
    }


    private fun setupFusedLocationClient() {
        with(LocationServices.getFusedLocationProviderClient(context!!)) {
            lastLocation.addOnSuccessListener {
                (activity as AppCompatActivity).obtainLocationVM()
                    .locationChanged("${it?.latitude} -- ${it?.longitude}")
            }
        }
    }


    private fun setupBindings() {
        with(binding) {
            locVM?.let { safeLocVM ->
                mapSearchB.setOnClickListener {
                    (activity as AppCompatActivity).obtainHomeVM()
                        .onMapSearchClicked(safeLocVM.location.value)
                }
            }
        }
    }


    private fun getFineLocationPermission() =
        ContextCompat.checkSelfPermission(
            context!!, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED


    private fun requestPermission() {
        requestPermissions(
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }


    companion object {

        fun newInstance() = MapFragment()

    }

}