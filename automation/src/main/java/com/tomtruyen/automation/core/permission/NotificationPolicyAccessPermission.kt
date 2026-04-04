package com.tomtruyen.automation.core.permission

import android.app.NotificationManager
import android.content.Intent
import android.os.Build
import android.provider.Settings
import com.tomtruyen.automation.R

data object NotificationPolicyAccessPermission: AutomationPermission.Intent(
    titleRes = R.string.automation_permission_notification_policy_title,
    messageRes = R.string.automation_permission_notification_policy_message,
    intent = PermissionIntent.Custom {
        Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
    },
    grantCheck = { context ->
        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager?.isNotificationPolicyAccessGranted == true
    },
    minSdk = Build.VERSION_CODES.M
)