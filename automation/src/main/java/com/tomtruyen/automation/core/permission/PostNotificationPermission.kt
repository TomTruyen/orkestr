package com.tomtruyen.automation.core.permission

import android.Manifest
import android.os.Build
import com.tomtruyen.automation.R

data object PostNotificationPermission : AutomationPermission.Runtime(
    permission = Manifest.permission.POST_NOTIFICATIONS,
    rationaleTitleRes = R.string.automation_permission_notifications_rationale_title,
    rationaleMessageRes = R.string.automation_permission_notifications_rationale_message,
    deniedTitleRes = R.string.automation_permission_notifications_denied_title,
    deniedMessageRes = R.string.automation_permission_notifications_denied_message,
    minSdk = Build.VERSION_CODES.TIRAMISU,
)
