package com.tomtruyen.automation.core.permission

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.content.ContextCompat
import androidx.annotation.StringRes
import com.tomtruyen.automation.R

sealed interface AutomationPermission {
    val minSdk: Int
    fun isGranted(context: Context): Boolean

    sealed class Runtime(
        val permission: String,
        @param:StringRes val rationaleTitleRes: Int,
        @param:StringRes val rationaleMessageRes: Int,
        @param:StringRes val deniedTitleRes: Int = rationaleTitleRes,
        @param:StringRes val deniedMessageRes: Int = rationaleMessageRes,
        val rationaleIntent: PermissionIntent = PermissionIntent.AppSettings,
        override val minSdk: Int = Build.VERSION_CODES.M,
    ) : AutomationPermission {
        @SuppressLint("ObsoleteSdkInt") // It isn't obsolete since we can configure the minSdk to another value
        override fun isGranted(context: Context): Boolean {
            return Build.VERSION.SDK_INT < minSdk ||
                ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
        }
    }

    sealed class Intent(
        @param:StringRes val titleRes: Int,
        @param:StringRes val messageRes: Int,
        val intent: PermissionIntent,
        val grantCheck: (Context) -> Boolean = { false },
        override val minSdk: Int = Build.VERSION_CODES.M
    ) : AutomationPermission {
        @SuppressLint("ObsoleteSdkInt") // It isn't obsolete since we can configure the minSdk to another value
        override fun isGranted(context: Context): Boolean {
            return Build.VERSION.SDK_INT < minSdk || grantCheck(context)
        }
    }
}

sealed interface PermissionIntent {
    fun createIntent(context: Context): Intent

    data object AppSettings : PermissionIntent {
        override fun createIntent(context: Context): Intent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", context.packageName, null)
        )
    }

    class Custom(
        private val factory: (Context) -> Intent
    ) : PermissionIntent {
        override fun createIntent(context: Context): Intent = factory(context)
    }
}
