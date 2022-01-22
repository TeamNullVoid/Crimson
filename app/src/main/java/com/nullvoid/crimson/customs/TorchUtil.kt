package com.nullvoid.crimson.customs

import android.content.Context
import android.content.pm.PackageManager
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraManager
import android.widget.Toast

class TorchUtil(private var context: Context) {

    private var cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    private var cameraId = cameraManager.cameraIdList[0]

    fun toggleTorch(b: Boolean) {
        try {
            cameraManager.setTorchMode(cameraId, b)
        } catch (e: CameraAccessException) {
            Toast.makeText(context, "Failed to access camera", Toast.LENGTH_LONG).show()
        }
    }

    fun hasFlash(): Boolean {
        return context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)
    }

}