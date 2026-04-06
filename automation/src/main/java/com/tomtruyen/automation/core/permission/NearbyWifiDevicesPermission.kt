package com.tomtruyen.automation.core.permission

import android.Manifest
import android.os.Build
import com.tomtruyen.automation.R

data object NearbyWifiDevicesPermission : AutomationPermission.Runtime(
    permission = Manifest.permission.NEARBY_WIFI_DEVICES,
    rationaleTitleRes = R.string.automation_permission_nearby_wifi_title,
    rationaleMessageRes = R.string.automation_permission_nearby_wifi_message,
    deniedTitleRes = R.string.automation_permission_nearby_wifi_denied_title,
    deniedMessageRes = R.string.automation_permission_nearby_wifi_denied_message,
    minSdk = Build.VERSION_CODES.TIRAMISU,
)
