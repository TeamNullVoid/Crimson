package com.nullvoid.crimson.fragments

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.nullvoid.crimson.R
import com.nullvoid.crimson.adapters.InfoWindowAdapter
import com.nullvoid.crimson.customs.*
import com.nullvoid.crimson.data.model.GeofenceModel
import com.nullvoid.crimson.data.viewModel.GeofenceViewModel
import com.nullvoid.crimson.databinding.FragmentMapsBinding
import com.nullvoid.crimson.services.LocationService
import java.util.*

class MapsFragment : Fragment(), OnMapReadyCallback, View.OnClickListener,
    GoogleMap.OnMarkerClickListener, GoogleMap.OnMapLongClickListener {

    private lateinit var binding: FragmentMapsBinding
    private lateinit var maps: GoogleMap
    private var myMarker: Marker? = null
    private lateinit var myAvatar: Bitmap
    private var locationService: LocationService? = null
    private var serviceBounded = false
    private lateinit var behaviour: BottomSheetBehavior<LinearLayout>
    private lateinit var viewModel: GeofenceViewModel
    private lateinit var geofenceHelper: GeofenceHelper
    private lateinit var geofencingClient: GeofencingClient
    private var placesMarker: Marker? = null
    private var placesCircle: Circle? = null

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
        viewModel = ViewModelProvider(this)[GeofenceViewModel::class.java]
        behaviour = BottomSheetBehavior.from(binding.placesSheet.placesSheetRoot)
        behaviour.state = BottomSheetBehavior.STATE_HIDDEN
        binding.placesSheet.placesSaveButton.setOnClickListener(saveGeofence)
        binding.placesSheet.placesRadiusSlider.addOnChangeListener { _, value, _ ->
            placesCircle?.radius = value.toDouble()
        }
        binding.placesSheet.placesRadiusSlider.setLabelFormatter {
            return@setLabelFormatter "${it.toInt()} meters"
        }
        binding.mapsLayers.setOnClickListener(this)
        binding.mapsMyLocation.setOnClickListener(this)
        myAvatar = Converter.drawableToBitmap(
            resources.displayMetrics,
            ContextCompat.getDrawable(
                requireContext(),
                R.drawable.ic_avatar
            )!!
        )

        geofencingClient = LocationServices.getGeofencingClient(requireActivity())
        geofenceHelper = GeofenceHelper(requireContext())
    }

    override fun onMapReady(maps: GoogleMap) {
        this.maps = maps
        maps.uiSettings.isMapToolbarEnabled = false
        maps.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(24.668887, 78.521063), 5f))
        maps.setInfoWindowAdapter(InfoWindowAdapter(requireContext()))
        maps.setOnMapLongClickListener(this)
        maps.setOnMarkerClickListener(this)
        viewModel.getPlaces().observe(this) {
            refreshMarkers(it)
        }
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

    private fun refreshMarkers(placesList: List<GeofenceModel>) {
        maps.clear()
        for (i in placesList) {
            maps.addMarker(
                MarkerOptions().title(i.name).snippet("").position(LatLng(i.latitude, i.longitude))
            )
            maps.addCircle(
                CircleOptions().center(LatLng(i.latitude, i.longitude)).radius(i.radius)
                    .fillColor(Color.parseColor("#33DC143C"))
                    .strokeColor(Color.parseColor("#FFDC143C"))
                    .strokeWidth(1f)
            )
        }
        if (myMarker != null) {
            myMarker?.remove()
            myMarker = maps.addMarker(MarkerOptions().apply {
                position(myMarker!!.position)
                title(getString(R.string.me))
                icon(BitmapDescriptorFactory.fromBitmap(myAvatar))
            })
        }
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

    override fun onMarkerClick(marker: Marker): Boolean {
        when (marker) {
            myMarker -> return false
            placesMarker -> {
                behaviour.state = BottomSheetBehavior.STATE_EXPANDED
                maps.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.position, 18f))
            }
            else -> {
                childFragmentManager.beginTransaction()
                    .add(
                        MarkerDetailFragment.getInstance(marker),
                        MarkerDetailFragment::class.java.simpleName
                    ).commit()
            }
        }
        return true
    }

    override fun onMapLongClick(latLng: LatLng) {
        addMarker(latLng)
        addCircle(latLng, binding.placesSheet.placesRadiusSlider.value.toDouble())
    }

    private fun addMarker(latLng: LatLng) {
        placesMarker?.remove()
        placesMarker = maps.addMarker(MarkerOptions().apply {
            position(latLng)
        })
    }

    private fun addCircle(latLng: LatLng, radius: Double) {
        placesCircle?.remove()
        placesCircle = maps.addCircle(CircleOptions().apply {
            center(latLng)
            radius(radius)
            fillColor(Color.parseColor("#33DC143C"))
            strokeColor(Color.parseColor("#FFDC143C"))
            strokeWidth(1f)
        })
    }

    private val saveGeofence = View.OnClickListener {
        val name = binding.placesSheet.placesNameField.text.toString().trim()
        if (name.isEmpty()) {
            binding.placesSheet.placesNameLayout.isErrorEnabled = true
            binding.placesSheet.placesNameLayout.error = getString(R.string.name_cant_empty)
        } else {
            binding.placesSheet.placesNameLayout.error = null
            binding.placesSheet.placesNameLayout.isErrorEnabled = false
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                if (ContextCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    addGeofence(
                        name.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() },
                        placesMarker?.position!!,
                        binding.placesSheet.placesRadiusSlider.value
                    )
                } else {
                    val dialog = Global.showLocationPermissionRational(requireContext())
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                        dialog.dismiss()
                        ActivityCompat.requestPermissions(
                            requireActivity(),
                            arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                            Constant.RC_BG_LOC_PERM
                        )
                    }
                }
            } else {
                addGeofence(
                    name.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() },
                    placesMarker?.position!!, binding.placesSheet.placesRadiusSlider.value
                )
            }
            behaviour.state = BottomSheetBehavior.STATE_HIDDEN
        }
    }

    private fun addGeofence(id: String, latLng: LatLng, radius: Float) {
        val model = GeofenceModel(
            id,
            latLng.latitude,
            latLng.longitude,
            radius.toDouble()
        )

        val geofence = geofenceHelper.getGeofence(
            id,
            latLng,
            radius,
            Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT
        )
        val request = geofenceHelper.getGeofencingRequest(geofence)
        val pendingIntent = geofenceHelper.getPendingIntent()

        if (!checkPermission()) {
            val dialog = Global.showLocationPermissionRational(requireContext())
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                dialog.dismiss()
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    Constant.RC_LOC_PERM
                )
            }
            return
        }
        if (pendingIntent != null) {
            geofencingClient.addGeofences(request, pendingIntent).addOnSuccessListener {
                viewModel.insert(model)
                placesMarker = null
                placesCircle = null
            }.addOnFailureListener {
                Toast.makeText(context, geofenceHelper.errorMessage(it), Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun checkPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }
}