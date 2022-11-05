package com.example.ayamjumpa

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Geocoder

import android.location.Location
import android.location.LocationRequest
import android.os.Bundle
import android.text.Editable
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil

import com.example.ayamjumpa.databinding.FragmentMapBinding
import java.util.*


class MapFragment : Fragment(), OnMapReadyCallback {

    var currentMarker: Marker? = null
    private lateinit var binding: FragmentMapBinding
    private lateinit var mMap: GoogleMap
    var fusedLocationProviderClient: FusedLocationProviderClient? = null
    var currentLocation: Location? = null
    var mLocationRequest:LocationRequest?=null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding =  DataBindingUtil.inflate(inflater,R.layout.fragment_map, container, false)

      return  binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())

        fetchLoaction()


    }


    private fun drawMarker(latlong: LatLng) {


        val marker = MarkerOptions().position(latlong).title("Disini lokasi saya")
            .snippet(getAddress(latlong.latitude, latlong.longitude)).draggable(true)

        mMap.animateCamera(CameraUpdateFactory.newLatLng(latlong))

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlong, 15f))
        currentMarker = mMap.addMarker(marker)

        currentMarker?.showInfoWindow()

        binding.alamatnya.text = getAddress(latlong.latitude, latlong.longitude)?.toEditable()


    }

    private fun getAddress(latitude: Double, longitude: Double): String? {
        val geocoder = Geocoder(requireContext(), Locale.getDefault())

        val address = geocoder.getFromLocation(latitude, longitude, 1)



        return address[0].getAddressLine(0)
    }


    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap


      mMap.uiSettings.isMyLocationButtonEnabled = false




        mMap.isMyLocationEnabled = true
        val latlong = LatLng(currentLocation?.latitude!!, currentLocation?.longitude!!)
        drawMarker(latlong)

        mMap.setOnMarkerDragListener(object : GoogleMap.OnMarkerDragListener {
            override fun onMarkerDrag(p0: Marker) {

            }

            override fun onMarkerDragStart(p0: Marker) {
                // binding.scrollView2.requestDisallowInterceptTouchEvent(true)
            }

            override fun onMarkerDragEnd(p0: Marker) {

                if (currentMarker != null) {
                    currentMarker?.remove()
                    val newLatLong = LatLng(p0.position.latitude, p0.position.longitude)

                    drawMarker(newLatLong)

                }
            }
        })
    }


    private fun fetchLoaction() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) !=
            PackageManager.PERMISSION_GRANTED
            &&
            ActivityCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) !=
            PackageManager.PERMISSION_GRANTED

        ) {

            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                1000
            )

            return

        } else {

            val task = fusedLocationProviderClient?.lastLocation

            task?.addOnCompleteListener { location ->

                if (location.result != null) {
                    this.currentLocation = location.result

                    val mapFragment =
                        childFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment



                    mapFragment.getMapAsync(this)


                }
            }


        }
    }

    fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)


}