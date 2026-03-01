package com.ibrain.flashlight

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Camera
import android.hardware.camera2.CameraManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.ibrain.flashlight.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var camera: Camera? = null
    private var isFlashOn = false
    private var hasFlash = false

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permission granted, do nothing, just let user toggle
        } else {
            Toast.makeText(this, getString(R.string.permission_required), Toast.LENGTH_LONG).show()
            // If user permanently denied, we might want to guide them to settings
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                showPermissionDeniedDialog()
            }
            binding.toggleFlashlight.isChecked = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        hasFlash = packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)

        if (!hasFlash) {
            Toast.makeText(this, getString(R.string.no_flash_error), Toast.LENGTH_LONG).show()
            binding.toggleFlashlight.isEnabled = false
            return
        }

        // Check and request permission immediately on start
        checkCameraPermission()

        binding.toggleFlashlight.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                if (hasPermission()) {
                    turnOnFlash()
                } else {
                    binding.toggleFlashlight.isChecked = false
                    checkCameraPermission()
                }
            } else {
                turnOffFlash()
            }
        }

        binding.btnHelp.setOnClickListener {
            showHelpDialog()
        }
    }

    private fun checkCameraPermission() {
        if (!hasPermission()) {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun hasPermission(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
    }

    private fun showHelpDialog() {
        AlertDialog.Builder(this)
            .setTitle(R.string.app_name)
            .setMessage("Đây là ứng dụng đèn pin đơn giản.\nNhấn nút giữa để bật/tắt đèn.")
            .setPositiveButton("OK", null)
            .show()
    }

    private fun showPermissionDeniedDialog() {
        AlertDialog.Builder(this)
            .setTitle("Cần quyền truy cập")
            .setMessage("Ứng dụng cần quyền Camera để sử dụng đèn pin. Vui lòng cấp quyền trong cài đặt ứng dụng.")
            .setPositiveButton("OK", null)
            .show()
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
                Toast.makeText(this, getString(R.string.camera_busy_error), Toast.LENGTH_SHORT).show()
                binding.toggleFlashlight.isChecked = false
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
                Toast.makeText(this, getString(R.string.legacy_camera_error), Toast.LENGTH_SHORT).show()
                binding.toggleFlashlight.isChecked = false
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

    override fun onResume() {
        super.onResume()
        // Re-check permission if user coming back from settings
        if (hasPermission()) {
            binding.toggleFlashlight.isEnabled = true
        }
    }

    override fun onPause() {
        super.onPause()
        if (isFlashOn) {
            binding.toggleFlashlight.isChecked = false
            turnOffFlash()
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
