package com.nullvoid.crimson.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.AlertDialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.slider.LabelFormatter
import com.nullvoid.crimson.R
import com.nullvoid.crimson.customs.Constant
import com.nullvoid.crimson.customs.Global
import com.nullvoid.crimson.databinding.FragmentShareLocationBinding
import com.nullvoid.crimson.services.LocSharingService
import com.nullvoid.crimson.services.StopLocService
import java.util.*

class ShareLocationFragment : BottomSheetDialogFragment(), View.OnClickListener {

    private lateinit var binding: FragmentShareLocationBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentShareLocationBinding.inflate(layoutInflater)
        init()
        return binding.root
    }

    private fun init() {
        binding.shareLocationSlider.setLabelFormatter(LabelFormatter { v ->
            val hour = (v / 60).toInt()
            val minute = (v - 60 * hour).toInt()
            val hr = if (hour == 1) "$hour hour" else if (hour == 0) "" else "$hour hours"
            val mi = if (minute == 0) "" else "$minute minutes"
            return@LabelFormatter "$hr $mi"
        })
        binding.shareLocationShareButton.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        if (v.id == R.id.shareLocationShareButton) {
            shareLiveLoc()
        }
    }

    @SuppressLint("MissingPermission")
    private fun shareLiveLoc() {
        val service = Intent(context, LocSharingService::class.java)
        val alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pendingIntent =
            PendingIntent.getService(context, 1, Intent(context, StopLocService::class.java), 0)

        val calendar = Calendar.getInstance()
        calendar.add(Calendar.MINUTE, binding.shareLocationSlider.value.toInt())

        val pref = context?.getSharedPreferences(Constant.SP_CRIMSON, Context.MODE_PRIVATE)

        with(pref?.edit()) {
            this?.putLong(Constant.RM_TIME, calendar.timeInMillis)
            this?.apply()
        }

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )
        if (hasPermission()) {
            context?.startForegroundService(service)
            dismiss()
        } else {
            val dialog = Global.showLocationPermissionRational(requireContext())
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                dialog.dismiss()
                requestPermissions(
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    Constant.RC_LOC_PERM
                )
            }
        }
    }

    private fun hasPermission(): Boolean {
        return context?.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constant.RC_LOC_PERM && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            shareLiveLoc()
        } else {
            MaterialAlertDialogBuilder(requireContext()).apply {
                setTitle(R.string.permission_denied)
                setMessage(R.string.locPermNeed)
                setPositiveButton(R.string.settings) { _, _ ->
                    val intent =
                        Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    intent.data = Uri.fromParts("package", context.packageName, null)
                    startActivity(intent)
                }
                setNegativeButton(R.string.cancel, null)
            }.show()
        }
    }

}