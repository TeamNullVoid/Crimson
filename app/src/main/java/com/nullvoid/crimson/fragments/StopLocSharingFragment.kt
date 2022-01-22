package com.nullvoid.crimson.fragments

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.nullvoid.crimson.R
import com.nullvoid.crimson.customs.Constant
import com.nullvoid.crimson.databinding.LayoutStopLocSharingBinding
import com.nullvoid.crimson.services.LocSharingService
import com.nullvoid.crimson.services.StopLocService
import java.text.SimpleDateFormat
import java.util.*

class StopLocSharingFragment : BottomSheetDialogFragment() {

    private lateinit var binding: LayoutStopLocSharingBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = LayoutStopLocSharingBinding.inflate(layoutInflater)
        init()
        return binding.root
    }

    private fun init() {
        val alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pendingIntent = PendingIntent.getService(context, 1, Intent(context, StopLocService::class.java), 0)

        val pref = context?.getSharedPreferences(Constant.SP_CRIMSON, Context.MODE_PRIVATE)

        val calendar = Calendar.getInstance()
        calendar.timeInMillis = pref?.getLong(Constant.RM_TIME, 0L)!!

        binding.stopLocText.text = getString(R.string.end_at, SimpleDateFormat("hh:mm:ss", Locale.ROOT).format(calendar.timeInMillis))

        binding.stopLocSharing.setOnClickListener {
            alarmManager.cancel(pendingIntent)
            context?.stopService(Intent(context, LocSharingService::class.java))
            dismiss()
        }
    }

}