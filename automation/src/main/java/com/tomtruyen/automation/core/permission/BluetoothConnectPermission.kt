package com.tomtruyen.automation.core.permission

import android.Manifest
import android.os.Build
import com.tomtruyen.automation.R

data object BluetoothConnectPermission : AutomationPermission.Runtime(
    permission = Manifest.permission.BLUETOOTH_CONNECT,
    rationaleTitleRes = R.string.automation_permission_bluetooth_connect_title,
    rationaleMessageRes = R.string.automation_permission_bluetooth_connect_message,
    deniedTitleRes = R.string.automation_permission_bluetooth_connect_denied_title,
    deniedMessageRes = R.string.automation_permission_bluetooth_connect_denied_message,
    minSdk = Build.VERSION_CODES.S,
)
