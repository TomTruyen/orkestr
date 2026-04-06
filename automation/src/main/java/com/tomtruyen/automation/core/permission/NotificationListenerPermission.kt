package com.tomtruyen.automation.core.permission

import android.content.ComponentName
import android.content.Intent
import android.os.Build
import android.provider.Settings
import com.tomtruyen.automation.R
import com.tomtruyen.automation.features.triggers.receiver.AutomationNotificationListenerService

data object NotificationListenerPermission : AutomationPermission.Intent(
    titleRes = R.string.automation_permission_notification_listener_title,
    messageRes = R.string.automation_permission_notification_listener_message,
    intent = PermissionIntent.Custom {
        Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
    },
    grantCheck = { context ->
        val enabledListeners = Settings.Secure.getString(context.contentResolver, "enabled_notification_listeners")
        val componentName = ComponentName(context, AutomationNotificationListenerService::class.java).flattenToString()
        enabledListeners?.split(':').orEmpty().contains(componentName)
    },
    minSdk = Build.VERSION_CODES.JELLY_BEAN_MR2,
)
