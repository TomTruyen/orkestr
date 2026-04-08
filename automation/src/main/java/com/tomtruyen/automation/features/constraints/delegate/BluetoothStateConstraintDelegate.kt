package com.tomtruyen.automation.features.constraints.delegate

import android.content.Context
import com.tomtruyen.automation.codegen.GenerateConstraintDelegate
import com.tomtruyen.automation.features.constraints.ConstraintType
import com.tomtruyen.automation.features.constraints.config.BluetoothStateConstraintConfig

@GenerateConstraintDelegate
class BluetoothStateConstraintDelegate(context: Context) :
    LiveStateConstraintDelegate<BluetoothStateConstraintConfig>(context) {
    override val type: ConstraintType = ConstraintType.BLUETOOTH_STATE

    override suspend fun evaluate(config: BluetoothStateConstraintConfig): Boolean =
        deviceStateReader.isBluetoothEnabled() == config.enabled
}
