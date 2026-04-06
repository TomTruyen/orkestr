package com.tomtruyen.orkestr.features.timebased.component

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.tomtruyen.orkestr.ui.timebased.R
import android.R as AndroidR

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun AutomationTimePickerDialog(hour: Int, minute: Int, onDismiss: () -> Unit, onConfirm: (Int, Int) -> Unit) {
    val timePickerState = rememberTimePickerState(
        initialHour = hour,
        initialMinute = minute,
        is24Hour = true,
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.automation_time_picker_dialog_title)) },
        text = {
            TimePicker(
                state = timePickerState,
                modifier = Modifier,
            )
        },
        confirmButton = {
            Button(onClick = { onConfirm(timePickerState.hour, timePickerState.minute) }) {
                Text(stringResource(R.string.automation_action_save_changes))
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text(stringResource(AndroidR.string.cancel))
            }
        },
    )
}
