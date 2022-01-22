package com.nullvoid.crimson

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.nullvoid.crimson.adapters.InfoWindowAdapter
import com.nullvoid.crimson.customs.Constant
import com.nullvoid.crimson.customs.Converter
import com.nullvoid.crimson.data.model.CrimsonExtras
import com.nullvoid.crimson.data.model.LocalCrimsonUser
import com.nullvoid.crimson.data.model.LocationExtras
import com.nullvoid.crimson.data.viewModel.CrimsonUserViewModel
import com.nullvoid.crimson.databinding.ActivityCrimsonContactBinding

class CrimsonContactActivity : AppCompatActivity(), OnMapReadyCallback {

    private var maps: GoogleMap? = null
    private lateinit var viewModel: CrimsonUserViewModel
    private lateinit var behaviour: BottomSheetBehavior<LinearLayout>
    private lateinit var binding: ActivityCrimsonContactBinding
    private var extrasKey: ListenerRegistration? = null
    private lateinit var avatarBitmap: Bitmap
    private var myMarker: Marker? = null
    private lateinit var uid: String
    private lateinit var user: LocalCrimsonUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCrimsonContactBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
    }

    private fun init() {
        user = intent.getSerializableExtra(Constant.EXTRAS_ID) as LocalCrimsonUser
        uid = user.basic.userId!!
        avatarBitmap = Converter.drawableToBitmap(
            resources.displayMetrics,
            ContextCompat.getDrawable(this, R.drawable.ic_avatar)!!
        )
        behaviour = BottomSheetBehavior.from(binding.crimsonContactMapSheet)
        viewModel = ViewModelProvider(this).get(CrimsonUserViewModel::class.java)
        viewModel.getUser(uid).observe(this) {
            if (it != null) {
                user = it
                updateUi()
            }
        }
        (supportFragmentManager.findFragmentById(R.id.crimsonContact_map) as SupportMapFragment).getMapAsync(
            this
        )
        binding.crimsonContactToolbar.setNavigationOnClickListener { finish() }
        binding.crimsonContactLocation.setOnClickListener {
            behaviour.state = BottomSheetBehavior.STATE_COLLAPSED
        }
        binding.crimsonContactMapToolbar.setNavigationOnClickListener {
            behaviour.state = BottomSheetBehavior.STATE_HIDDEN
        }
        behaviour.addBottomSheetCallback(sheetStateListener)
    }

    override fun onStart() {
        super.onStart()
        extrasKey = Firebase.firestore.document("users/$uid/private/extras")
            .addSnapshotListener { data, error ->
                if (error != null) {
                    Snackbar.make(binding.root, error.message.toString(), Snackbar.LENGTH_LONG)
                        .show()
                } else {
                    if (data != null) {
                        val extras = data.toObject(CrimsonExtras::class.java)!!
                        viewModel.update(uid, extras)
                    }
                }
            }

        Firebase.database.getReference("location/$uid").addValueEventListener(locListener)
    }

    override fun onDestroy() {
        super.onDestroy()
        extrasKey?.remove()
        Firebase.database.getReference("location/$uid").removeEventListener(locListener)
    }

    private val locListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val loc = snapshot.getValue(LocationExtras::class.java)
            loc ?: return
            viewModel.update(uid, loc)
        }

        override fun onCancelled(error: DatabaseError) {
            Snackbar.make(binding.root, error.message, Snackbar.LENGTH_LONG).show()
        }
    }

    private val sheetStateListener = object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View, newState: Int) {
            when (newState) {
                BottomSheetBehavior.STATE_EXPANDED -> {
                    behaviour.isDraggable = false
                    binding.crimsonContactMapSheet.background =
                        ColorDrawable(Color.parseColor("#FFF71C43"))
                }
                else -> {
                    behaviour.isDraggable = true
                    binding.crimsonContactMapSheet.background = ContextCompat.getDrawable(
                        this@CrimsonContactActivity,
                        R.drawable.shape_round_only_top
                    )
                }
            }
        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {}
    }

    override fun onMapReady(map: GoogleMap) {
        maps = map
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(24.668887, 78.521063), 5f))
        map.setInfoWindowAdapter(InfoWindowAdapter(this))
        map.uiSettings.isMapToolbarEnabled = false
        updateUi()
    }

    private fun updateUi() {
        binding.crimsonContactToolbar.title = user.basic.userName ?: user.basic.userPhone
        binding.crimsonContactName.text = user.basic.userName ?: "No Name"
        binding.crimsonContactEmail.text = user.basic.userPhone
        if (user.crimsonExtras?.inEmergency == true) {
            binding.crimsonContactInEmergency.setText(R.string.emergency)
            binding.crimsonContactInEmergency.setChipIconResource(R.drawable.ic_error)
        } else {
            binding.crimsonContactInEmergency.setText(R.string.safe)
            binding.crimsonContactInEmergency.setChipIconResource(R.drawable.ic_check)
        }
        maps?.clear()

        if (user.locationExtras != null && user.locationExtras?.latitude != null && user.locationExtras?.longitude != null) {
            val latLng = LatLng(user.locationExtras?.latitude!!, user.locationExtras?.longitude!!)
            binding.crimsonContactLocation.setChipIconResource(R.drawable.ic_location)
            binding.crimsonContactLocation.text = getString(R.string.location_available)
            maps?.clear()
            myMarker = maps?.addMarker(MarkerOptions().apply {
                position(latLng)
                title(user.basic.userName)
                snippet(getString(R.string.last_updated, user.locationExtras?.lastUpdate))
            })
            myMarker?.setIcon(BitmapDescriptorFactory.fromBitmap(avatarBitmap))
            maps?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 20f))
        }
    }

}