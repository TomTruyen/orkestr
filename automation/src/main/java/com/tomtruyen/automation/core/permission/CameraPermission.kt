package com.tomtruyen.automation.core.permission

import android.Manifest
import com.tomtruyen.automation.R

data object CameraPermission : AutomationPermission.Runtime(
    permission = Manifest.permission.CAMERA,
    rationaleTitleRes = R.string.automation_permission_camera_title,
    rationaleMessageRes = R.string.automation_permission_camera_message,
    deniedTitleRes = R.string.automation_permission_camera_denied_title,
    deniedMessageRes = R.string.automation_permission_camera_denied_message,
)
