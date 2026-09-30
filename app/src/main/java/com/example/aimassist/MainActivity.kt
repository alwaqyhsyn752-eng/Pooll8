package com.example.aimassist

import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.aimassist.databinding.ActivityMainBinding
import com.example.aimassist.service.OverlayService
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val overlayPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (Settings.canDrawOverlays(this)) requestCapturePermission()
        else Toast.makeText(this, "Overlay permission denied", Toast.LENGTH_SHORT).show()
    }

    private val capturePermissionLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK && result.data != null) {
            val intent = Intent(this, OverlayService::class.java).apply {
                putExtra(OverlayService.EXTRA_RESULT_CODE, result.resultCode)
                putExtra(OverlayService.EXTRA_DATA, result.data)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) startForegroundService(intent)
            else startService(intent)
            binding.statusText.text = getString(R.string.status_running)
        } else {
            binding.statusText.text = getString(R.string.status_denied)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.startButton.setOnClickListener {
            if (!Settings.canDrawOverlays(this)) {
                overlayPermissionLauncher.launch(
                    Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:$packageName"))
                )
            } else requestCapturePermission()
        }

        binding.stopButton.setOnClickListener {
            stopService(Intent(this, OverlayService::class.java))
            binding.statusText.text = getString(R.string.status_stopped)
        }
    }

    private fun requestCapturePermission() {
        val mgr = getSystemService(MediaProjectionManager::class.java)
        capturePermissionLauncher.launch(mgr.createScreenCaptureIntent())
    }
}
