package com.tomtruyen.orkestr.common.permission

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.core.app.ActivityCompat
import com.tomtruyen.automation.core.permission.AutomationPermission
import com.tomtruyen.orkestr.R

class AutomationPermissionManager private constructor(
    private val context: Context,
    private val activity: Activity,
    private val requestPermissions: (Array<String>) -> Unit,
) {
    private var dialogState by mutableStateOf<PermissionDialogState?>(null)
    private var pendingRequest: PermissionRequest? = null

    fun request(permissions: List<AutomationPermission>, onGranted: () -> Unit) {
        val activePermissions = permissions.filter { Build.VERSION.SDK_INT >= it.minSdk }
        if (activePermissions.isEmpty()) {
            onGranted()
            return
        }

        val intentPermissions = activePermissions.filterIsInstance<AutomationPermission.Intent>()
        val missingIntentPermissions = intentPermissions.filterNot { it.isGranted(context) }
        if (missingIntentPermissions.isNotEmpty()) {
            dialogState = PermissionDialogState.Intent(
                permissions = missingIntentPermissions,
                onGranted = onGranted,
            )
            return
        }

        val runtimePermissions = activePermissions.filterIsInstance<AutomationPermission.Runtime>()
        val missingRuntimePermissions = runtimePermissions.filterNot { it.isGranted(context) }

        if (missingRuntimePermissions.isEmpty()) {
            onGranted()
            return
        }

        pendingRequest = PermissionRequest(
            permissions = missingRuntimePermissions,
            onGranted = onGranted,
        )

        dialogState = if (missingRuntimePermissions.any { permission ->
                ActivityCompat.shouldShowRequestPermissionRationale(activity, permission.permission)
            }
        ) {
            PermissionDialogState.Rationale(
                permissions = missingRuntimePermissions,
                onGranted = onGranted,
            )
        } else {
            null
        }

        if (dialogState == null) {
            launchPermissionRequest(missingRuntimePermissions)
        }
    }

    @Composable
    fun RenderDialogs() {
        dialogState?.let { state ->
            when (state) {
                is PermissionDialogState.Rationale -> PermissionDialog(
                    title = stringResource(state.permissions.first().rationaleTitleRes),
                    message = state.permissions.joinToString(separator = "\n\n") { permission ->
                        context.getString(permission.rationaleMessageRes)
                    },
                    confirmLabel = stringResource(R.string.automation_permission_action_continue),
                    dismissLabel = stringResource(R.string.automation_action_close),
                    onConfirm = {
                        dialogState = null
                        launchPermissionRequest(state.permissions)
                    },
                    onDismiss = {
                        dialogState = null
                        pendingRequest = null
                    },
                )

                is PermissionDialogState.Denied -> PermissionDialog(
                    title = stringResource(state.permissions.first().deniedTitleRes),
                    message = state.permissions.joinToString(separator = "\n\n") { permission ->
                        context.getString(permission.deniedMessageRes)
                    },
                    confirmLabel = stringResource(R.string.automation_permission_action_open_settings),
                    dismissLabel = stringResource(R.string.automation_permission_action_retry),
                    onConfirm = {
                        dialogState = null
                        pendingRequest = null
                        context.startActivity(state.permissions.first().rationaleIntent.createIntent(context))
                    },
                    onDismiss = {
                        dialogState = null
                        launchPermissionRequest(state.permissions)
                    },
                )

                is PermissionDialogState.Intent -> PermissionDialog(
                    title = stringResource(state.permissions.first().titleRes),
                    message = state.permissions.joinToString(separator = "\n\n") { permission ->
                        context.getString(permission.messageRes)
                    },
                    confirmLabel = stringResource(R.string.automation_permission_action_open_settings),
                    dismissLabel = stringResource(R.string.automation_action_close),
                    onConfirm = {
                        dialogState = null
                        context.startActivity(state.permissions.first().intent.createIntent(context))
                    },
                    onDismiss = { dialogState = null },
                )
            }
        }
    }

    fun onPermissionsResult(result: Map<String, Boolean>) {
        val request = pendingRequest ?: return
        val deniedPermissions = request.permissions.filter { permission ->
            result[permission.permission] != true && !permission.isGranted(context)
        }

        pendingRequest = null

        if (deniedPermissions.isEmpty()) {
            request.onGranted()
        } else {
            dialogState = PermissionDialogState.Denied(
                permissions = deniedPermissions,
                onGranted = request.onGranted,
            )
        }
    }

    private fun launchPermissionRequest(permissions: List<AutomationPermission.Runtime>) {
        requestPermissions(permissions.map { it.permission }.toTypedArray())
    }

    private data class PermissionRequest(val permissions: List<AutomationPermission.Runtime>, val onGranted: () -> Unit)

    private sealed interface PermissionDialogState {
        val onGranted: () -> Unit

        data class Rationale(val permissions: List<AutomationPermission.Runtime>, override val onGranted: () -> Unit) :
            PermissionDialogState

        data class Denied(val permissions: List<AutomationPermission.Runtime>, override val onGranted: () -> Unit) :
            PermissionDialogState

        data class Intent(val permissions: List<AutomationPermission.Intent>, override val onGranted: () -> Unit) :
            PermissionDialogState
    }

    companion object {
        @Composable
        fun remember(context: Context): AutomationPermissionManager {
            val activity = context.findActivity() ?: error("Activity context is required for permissions.")
            lateinit var manager: AutomationPermissionManager
            val launcher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestMultiplePermissions(),
            ) { result ->
                manager.onPermissionsResult(result)
            }
            manager = remember(context, activity) {
                AutomationPermissionManager(
                    context = context,
                    activity = activity,
                    requestPermissions = launcher::launch,
                )
            }
            return manager
        }
    }
}

@Composable
private fun PermissionDialog(
    title: String,
    message: String,
    confirmLabel: String,
    dismissLabel: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = { Text(message) },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text(confirmLabel)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text(dismissLabel)
            }
        },
    )
}

tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
