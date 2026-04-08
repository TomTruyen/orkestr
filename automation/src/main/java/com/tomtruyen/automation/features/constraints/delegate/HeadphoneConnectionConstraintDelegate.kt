package com.tomtruyen.automation.features.constraints.delegate

import android.content.Context
import com.tomtruyen.automation.codegen.GenerateConstraintDelegate
import com.tomtruyen.automation.features.constraints.ConstraintType
import com.tomtruyen.automation.features.constraints.config.HeadphoneConnectionConstraintConfig

@GenerateConstraintDelegate
class HeadphoneConnectionConstraintDelegate(context: Context) :
    LiveStateConstraintDelegate<HeadphoneConnectionConstraintConfig>(context) {
    override val type: ConstraintType = ConstraintType.HEADPHONE_CONNECTION

    override suspend fun evaluate(config: HeadphoneConnectionConstraintConfig): Boolean =
        deviceStateReader.isHeadphonesConnected() == config.connected
}
