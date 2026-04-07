package com.tomtruyen.orkestr

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.tomtruyen.automation.core.AutomationForegroundService
import com.tomtruyen.orkestr.navigation.AppNavigation
import com.tomtruyen.orkestr.ui.theme.OrkestrTheme

class MainActivity : ComponentActivity() {
    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) {
        // Permission result is handled by the system
        // If denied, the foreground service notification will still work on older Android versions
        // We don't need to do anything here as we don't want to force the user to grant permission
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            OrkestrTheme {
                AppNavigation()
            }
        }
    }

    override fun onStart() {
        super.onStart()

        // Request notification permission if not already granted (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS,
                ) != android.content.pm.PackageManager.PERMISSION_GRANTED
            ) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        AutomationForegroundService.start(this)
    }
}
