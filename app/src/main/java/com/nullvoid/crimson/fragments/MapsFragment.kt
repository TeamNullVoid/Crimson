package com.nullvoid.crimson.fragments

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Location
import android.os.Bundle
import android.os.IBinder
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.nullvoid.crimson.R
import com.nullvoid.crimson.adapters.InfoWindowAdapter
import com.nullvoid.crimson.customs.Constant
import com.nullvoid.crimson.customs.Converter
import com.nullvoid.crimson.customs.Global
import com.nullvoid.crimson.customs.LocationListener
import com.nullvoid.crimson.databinding.FragmentMapsBinding
import com.nullvoid.crimson.services.LocationService

class MapsFragment : Fragment(), OnMapReadyCallback, View.OnClickListener {
    private lateinit var binding: FragmentMapsBinding
    private lateinit var maps: GoogleMap
    private var myMarker: Marker? = null
    private lateinit var myAvatar: Bitmap
    private var locationService: LocationService? = null
    private var serviceBounded = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMapsBinding.inflate(layoutInflater)
        val fragment = childFragmentManager.findFragmentById(R.id.maps_holder) as SupportMapFragment
        fragment.getMapAsync(this)
        init()
        return binding.root
    }

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            serviceBounded = true
            locationService = (service as LocationService.LocationBinder).instance()
            doLocationWork()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            serviceBounded = false
            locationService = null
        }
    }

    private fun doLocationWork() {
        if (locationService!!.allProvidersDisabled()) {
            showSnack(getString(R.string.turn_on_gps))
        }
        locationService?.initialize(locationListener)
        locationService?.startUpdates()
    }

    override fun onStart() {
        super.onStart()
        val intent = Intent(context, LocationService::class.java)
        context?.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (serviceBounded) {
            locationService?.stopUpdates()
            context?.unbindService(serviceConnection)
            serviceBounded = false
        }
    }

    private fun init() {
        binding.mapsLayers.setOnClickListener(this)
        binding.mapsMyLocation.setOnClickListener(this)
        myAvatar = Converter.drawableToBitmap(
            resources.displayMetrics,
            ContextCompat.getDrawable(
                requireContext(),
                R.drawable.ic_avatar
            )!!
        )
    }

    override fun onMapReady(maps: GoogleMap) {
        this.maps = maps
        maps.uiSettings.isMapToolbarEnabled = false
        maps.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(24.668887, 78.521063), 5f))
        maps.setInfoWindowAdapter(InfoWindowAdapter(requireContext()))
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.maps_layers -> showLayersMenu()
            R.id.maps_myLocation -> animateToMyLocation()
        }
    }

    private fun animateToMyLocation() {
        if (locationService?.hasPermission() == false) {
            askPermission()
            return
        }

        if (locationService?.allProvidersDisabled() == true) {
            showSnack(getString(R.string.turn_on_gps))
            return
        }

        if (myMarker != null) {
            maps.animateCamera(CameraUpdateFactory.newLatLngZoom(myMarker!!.position, 17f))
        }
    }

    private fun showLayersMenu() {
        val layers = resources.getStringArray(R.array.layers)
        MaterialAlertDialogBuilder(requireContext()).apply {
            setTitle(R.string.change_layer)
            setItems(layers) { _, v ->
                when (v) {
                    0 -> maps.mapType = GoogleMap.MAP_TYPE_NORMAL
                    1 -> maps.mapType = GoogleMap.MAP_TYPE_SATELLITE
                    2 -> maps.mapType = GoogleMap.MAP_TYPE_HYBRID
                }
            }
            show()
        }
    }

    private fun askPermission() {
        val dialog = Global.showLocationPermissionRational(requireContext())
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            dialog.dismiss()
            requestPermissions(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                Constant.RC_LOC_PERM
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constant.RC_LOC_PERM) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationService?.startUpdates()
            } else {
                Log.e("Main", "Called")
                Global.showMessage(requireContext())
            }
        }
    }


    private fun showSnack(msg: String) {
        val snack = Snackbar.make(binding.root, msg, Snackbar.LENGTH_INDEFINITE)
        snack.setAction(R.string.settings) {
            val settingsIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(settingsIntent)
        }
        snack.show()
    }

    private val locationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            myMarker?.remove()
            myMarker = maps.addMarker(MarkerOptions().apply {
                position(LatLng(location.latitude, location.longitude))
                title(getString(R.string.me))
                snippet(getString(R.string.accuracy, "${location.accuracy} meters"))
                icon(BitmapDescriptorFactory.fromBitmap(myAvatar))
            })
        }

        override fun onProviderDisabled(provider: String) {
            if (locationService?.allProvidersDisabled() == true)
                showSnack(getString(R.string.turn_on_gps))
        }

        override fun onProviderEnabled(provider: String) {}

        override fun onStatusChanged(provider: String?, status: Int) {}

        override fun onPermissionDenied() {
            Snackbar.make(binding.root, R.string.perm_req, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.ok) {
                    askPermission()
                }.show()
        }
    }
}