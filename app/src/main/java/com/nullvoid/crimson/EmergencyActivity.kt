package com.nullvoid.crimson


import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.nullvoid.crimson.customs.Constant
import com.nullvoid.crimson.customs.Global
import com.nullvoid.crimson.databinding.ActivityEmergencyBinding
import com.nullvoid.crimson.services.EmergencyService


class EmergencyActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityEmergencyBinding
    private var inEmergency: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEmergencyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
    }

    private fun init() {
        inEmergency = Global.isMyServiceRunning(this, EmergencyService::class.java)
        binding.emergencyEmergencyButton.setOnClickListener(this)
        binding.emergencyEmergencyToolbar.setNavigationOnClickListener { finish() }
        binding.emergencyEmergencyCallList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.emergencyEmergencyCallList.hasFixedSize()
        emergencyUI(inEmergency)
        if (inEmergency) binding.emergencyTimer.visibility = View.GONE
        else{
            binding.emergencyEmergencyButton.callOnClick()
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.emergencyEmergencyButton -> {
                inEmergency = !inEmergency
                emergencyUI(inEmergency)
                if (inEmergency) {
                    binding.emergencyTimer.visibility = View.VISIBLE
                    timer.start()
                } else {
                    binding.emergencyTimer.visibility = View.GONE
                    timer.cancel()
                    stopService(Intent(this, EmergencyService::class.java))
                }
            }
        }
    }

    private val timer = object : CountDownTimer(5000, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            binding.emergencyTimer.text = ((millisUntilFinished / 1000) + 1).toString()
        }

        override fun onFinish() {
            inEmergency = true
            binding.emergencyTimer.visibility = View.GONE
            startService()
            emergencyUI(inEmergency)
        }
    }

    private fun startService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(Intent(this, EmergencyService::class.java))
        } else {
            startService(Intent(this, EmergencyService::class.java))
        }
    }

    private fun emergencyUI(value: Boolean) {
        if (value) {
            binding.emergencyEmergencyButton.setText(R.string.stop)
            binding.emergencyEmergencyButton.setIconResource(R.drawable.ic_stop)
        } else {
            binding.emergencyEmergencyButton.setText(R.string.emergency)
            binding.emergencyEmergencyButton.setIconResource(R.drawable.ic_ring)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        timer.cancel()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constant.RC_PERMISSION) {
            if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Global.showMessage(this)
            }
        }
    }

}