package com.tomtruyen.automation.core.permission

import android.Manifest
import com.tomtruyen.automation.R

data object FineLocationPermission : AutomationPermission.Runtime(
    permission = Manifest.permission.ACCESS_FINE_LOCATION,
    rationaleTitleRes = R.string.automation_permission_fine_location_rationale_title,
    rationaleMessageRes = R.string.automation_permission_fine_location_rationale_message,
    deniedTitleRes = R.string.automation_permission_fine_location_denied_title,
    deniedMessageRes = R.string.automation_permission_fine_location_denied_message,
)
