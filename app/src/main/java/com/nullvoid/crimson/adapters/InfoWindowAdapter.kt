package com.nullvoid.crimson.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.nullvoid.crimson.databinding.LayoutInfoWindowBinding

class InfoWindowAdapter(private var ctx: Context) : GoogleMap.InfoWindowAdapter {

    private lateinit var binding: LayoutInfoWindowBinding

    override fun getInfoWindow(marker: Marker): View {
        val inflater = LayoutInflater.from(ctx)
        binding = LayoutInfoWindowBinding.inflate(inflater)
        binding.infoWindowTitle.text = marker.title
        if (marker.snippet?.isNotEmpty() == true)
            binding.infoWindowSummary.text = marker.snippet
        else{
            binding.infoWindowSummary.visibility = View.GONE
        }
        return binding.root
    }

    override fun getInfoContents(marker: Marker): View? {
        return null
    }
}