package com.ibrain.flashlight

import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Camera
import android.hardware.camera2.CameraManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import android.widget.ToggleButton
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private var camera: Camera? = null
    private var isFlashOn = false
    private var hasFlash = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        hasFlash = packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)

        if (!hasFlash) {
            Toast.makeText(this, "Device has no flash!", Toast.LENGTH_LONG).show()
            return
        }

        val toggleButton = findViewById<ToggleButton>(R.id.toggleFlashlight)
        toggleButton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                turnOnFlash()
            } else {
                turnOffFlash()
            }
        }
    }

    private fun turnOnFlash() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                val camManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
                val cameraId = camManager.cameraIdList[0]
                camManager.setTorchMode(cameraId, true)
                isFlashOn = true
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            // Legacy Camera API for Android < 6.0
            try {
                if (camera == null) {
                    camera = Camera.open()
                }
                val params = camera?.parameters
                params?.flashMode = Camera.Parameters.FLASH_MODE_TORCH
                camera?.parameters = params
                camera?.startPreview()
                isFlashOn = true
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun turnOffFlash() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                val camManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
                val cameraId = camManager.cameraIdList[0]
                camManager.setTorchMode(cameraId, false)
                isFlashOn = false
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            // Legacy Camera API
            try {
                val params = camera?.parameters
                params?.flashMode = Camera.Parameters.FLASH_MODE_OFF
                camera?.parameters = params
                camera?.stopPreview()
                camera?.release()
                camera = null
                isFlashOn = false
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onStop() {
        super.onStop()
        if (isFlashOn) {
            turnOffFlash()
        }
    }
}
