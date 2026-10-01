package com.example.aimassist

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.media.projection.MediaProjectionManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.aimassist.databinding.ActivityMainBinding
import com.example.aimassist.service.OverlayService
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    // صلاحية الإشعارات (Android 13+)
    private val notifPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { /* نتجاهل النتيجة */ checkOverlayPermission() }

    // صلاحية Overlay
    private val overlayPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (Settings.canDrawOverlays(this)) requestCapturePermission()
        else Toast.makeText(this, "Overlay permission required", Toast.LENGTH_LONG).show()
    }

    // صلاحية التقاط الشاشة
    private val capturePermissionLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK && result.data != null) {
            try {
                val intent = Intent(this, OverlayService::class.java).apply {
                    putExtra(OverlayService.EXTRA_RESULT_CODE, result.resultCode)
                    putExtra(OverlayService.EXTRA_DATA, result.data)
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(intent)
                } else {
                    startService(intent)
                }
                binding.statusText.text = getString(R.string.status_running)
            } catch (t: Throwable) {
                Toast.makeText(this, "Start error: ${t.message}", Toast.LENGTH_LONG).show()
            }
        } else {
            binding.statusText.text = getString(R.string.status_denied)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            binding = ActivityMainBinding.inflate(layoutInflater)
            setContentView(binding.root)

            binding.startButton.setOnClickListener { checkNotificationPermission() }

            binding.stopButton.setOnClickListener {
                try {
                    stopService(Intent(this, OverlayService::class.java))
                } catch (_: Throwable) {}
                binding.statusText.text = getString(R.string.status_stopped)
            }
        } catch (t: Throwable) {
            Toast.makeText(this, "Init error: ${t.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(
                this, Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (!granted) {
                notifPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                return
            }
        }
        checkOverlayPermission()
    }

    private fun checkOverlayPermission() {
        if (!Settings.canDrawOverlays(this)) {
            try {
                overlayPermissionLauncher.launch(
                    Intent(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:$packageName")
                    )
                )
            } catch (_: Throwable) {
                Toast.makeText(this, "Cannot open overlay settings", Toast.LENGTH_LONG).show()
            }
        } else {
            requestCapturePermission()
        }
    }

    private fun requestCapturePermission() {
        try {
            val mgr = getSystemService(MediaProjectionManager::class.java)
            capturePermissionLauncher.launch(mgr.createScreenCaptureIntent())
        } catch (t: Throwable) {
            Toast.makeText(this, "Capture error: ${t.message}", Toast.LENGTH_LONG).show()
        }
    }
}
