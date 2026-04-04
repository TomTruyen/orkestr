package com.tomtruyen.automation.core.permission

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.annotation.StringRes
import com.tomtruyen.automation.R

sealed interface AutomationPermission {
    val minSdk: Int

    data class Runtime(
        val permission: String,
        @param:StringRes val rationaleTitleRes: Int,
        @param:StringRes val rationaleMessageRes: Int,
        @param:StringRes val deniedTitleRes: Int = rationaleTitleRes,
        @param:StringRes val deniedMessageRes: Int = rationaleMessageRes,
        val rationaleIntent: PermissionIntent = PermissionIntent.AppSettings,
        override val minSdk: Int = Build.VERSION_CODES.M,
    ) : AutomationPermission

    data class Intent(
        @param:StringRes val titleRes: Int,
        @param:StringRes val messageRes: Int,
        val intent: PermissionIntent,
        override val minSdk: Int = Build.VERSION_CODES.M
    ) : AutomationPermission
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

object AutomationPermissions {
    val postNotifications = AutomationPermission.Runtime(
        permission = Manifest.permission.POST_NOTIFICATIONS,
        rationaleTitleRes = R.string.automation_permission_notifications_rationale_title,
        rationaleMessageRes = R.string.automation_permission_notifications_rationale_message,
        deniedTitleRes = R.string.automation_permission_notifications_denied_title,
        deniedMessageRes = R.string.automation_permission_notifications_denied_message,
        minSdk = Build.VERSION_CODES.TIRAMISU
    )
}
