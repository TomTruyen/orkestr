package com.tomtruyen.automation.features.constraints.delegate

import android.content.Context
import com.tomtruyen.automation.codegen.GenerateConstraintDelegate
import com.tomtruyen.automation.features.constraints.ConstraintType
import com.tomtruyen.automation.features.constraints.config.CallStateConstraintConfig

@GenerateConstraintDelegate
class CallStateConstraintDelegate(context: Context) : LiveStateConstraintDelegate<CallStateConstraintConfig>(context) {
    override val type: ConstraintType = ConstraintType.CALL_STATE

    override suspend fun evaluate(config: CallStateConstraintConfig): Boolean =
        deviceStateReader.isCallActive() == config.inCall
}
