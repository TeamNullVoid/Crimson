package com.nullvoid.crimson.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.Marker
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.nullvoid.crimson.R
import com.nullvoid.crimson.data.CrimsonDatabase
import com.nullvoid.crimson.databinding.FragmentMarkerDetailBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MarkerDetailFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentMarkerDetailBinding
    private var marker: Marker? = null

    companion object {
        fun getInstance(marker: Marker): MarkerDetailFragment {
            val frag = MarkerDetailFragment()
            frag.marker = marker
            return frag
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMarkerDetailBinding.inflate(layoutInflater)
        init(marker!!)
        return binding.root
    }

    private fun init(marker: Marker) {
        binding.markerDetailTitle.text = marker.title
        binding.markerDetailSubtitle.text =
            getString(R.string.position, marker.position.latitude, marker.position.longitude)
        binding.markerDetailDelete.setOnClickListener {
            context?.let {
                val title = marker.title
                if (title != null) {
                    LocationServices.getGeofencingClient(it).removeGeofences(arrayListOf(title))
                        .addOnSuccessListener { _ ->
                            GlobalScope.launch(Dispatchers.IO) {
                                CrimsonDatabase.getInstance(it).geofenceDao.delete(title)
                                launch(Dispatchers.Main) { dismiss() }
                            }
                        }.addOnFailureListener { _ ->
                            Toast.makeText(it, "Failed to remove Geofence", Toast.LENGTH_LONG)
                                .show()
                        }
                }
            }
        }
    }

}