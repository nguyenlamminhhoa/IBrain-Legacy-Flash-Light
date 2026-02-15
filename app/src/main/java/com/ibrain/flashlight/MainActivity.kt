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
                Toast.makeText(this, "Camera is busy or in use", Toast.LENGTH_SHORT).show()
            }
        } else {
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
                Toast.makeText(this, "Legacy Camera Error", Toast.LENGTH_SHORT).show()
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
            try {
                camera?.let {
                    val params = it.parameters
                    params?.flashMode = Camera.Parameters.FLASH_MODE_OFF
                    it.parameters = params
                    it.stopPreview()
                    it.release()
                    camera = null
                    isFlashOn = false
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        // Giải phóng camera khi ứng dụng không còn ở trên màn hình
        if (isFlashOn) {
            // Tùy chọn: bạn có thể để đèn sáng tiếp hoặc tắt đi
            // turnOffFlash() 
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (camera != null) {
            camera?.release()
            camera = null
        }
    }
}
