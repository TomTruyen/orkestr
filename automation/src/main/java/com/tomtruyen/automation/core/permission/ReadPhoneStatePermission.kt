package com.tomtruyen.automation.core.permission

import android.Manifest
import com.tomtruyen.automation.R

data object ReadPhoneStatePermission : AutomationPermission.Runtime(
    permission = Manifest.permission.READ_PHONE_STATE,
    rationaleTitleRes = R.string.automation_permission_read_phone_state_title,
    rationaleMessageRes = R.string.automation_permission_read_phone_state_message,
    deniedTitleRes = R.string.automation_permission_read_phone_state_denied_title,
    deniedMessageRes = R.string.automation_permission_read_phone_state_denied_message,
)
