package com.tomtruyen.automation.core.permission

import android.Manifest
import android.os.Build
import com.tomtruyen.automation.R

data object BackgroundLocationPermission :
    AutomationPermission.Runtime(
        permission = Manifest.permission.ACCESS_BACKGROUND_LOCATION,
        rationaleTitleRes = R.string.automation_permission_background_location_rationale_title,
        rationaleMessageRes = R.string.automation_permission_background_location_rationale_message,
        deniedTitleRes = R.string.automation_permission_background_location_denied_title,
        deniedMessageRes = R.string.automation_permission_background_location_denied_message,
        minSdk = Build.VERSION_CODES.Q,
    )
