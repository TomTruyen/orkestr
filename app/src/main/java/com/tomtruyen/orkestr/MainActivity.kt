package com.tomtruyen.orkestr

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.tomtruyen.automation.core.AutomationForegroundService
import com.tomtruyen.orkestr.navigation.AppNavigation
import com.tomtruyen.orkestr.ui.theme.OrkestrTheme

class MainActivity : ComponentActivity() {
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
        AutomationForegroundService.start(this)
    }
}
