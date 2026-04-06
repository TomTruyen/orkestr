package com.tomtruyen.automation.core.permission

import android.app.AppOpsManager
import android.content.Intent
import android.os.Build
import android.provider.Settings
import com.tomtruyen.automation.R

data object UsageAccessPermission : AutomationPermission.Intent(
    titleRes = R.string.automation_permission_usage_access_title,
    messageRes = R.string.automation_permission_usage_access_message,
    intent = PermissionIntent.Custom {
        Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
    },
    grantCheck = { context ->
        val appOpsManager = context.getSystemService(AppOpsManager::class.java)
        appOpsManager?.unsafeCheckOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            android.os.Process.myUid(),
            context.packageName,
        ) == AppOpsManager.MODE_ALLOWED
    },
    minSdk = Build.VERSION_CODES.LOLLIPOP,
)
