package com.tomtruyen.automation.features.constraints.delegate

import android.content.Context
import com.tomtruyen.automation.codegen.GenerateConstraintDelegate
import com.tomtruyen.automation.features.constraints.ConstraintType
import com.tomtruyen.automation.features.constraints.config.ScreenStateConstraintConfig

@GenerateConstraintDelegate
class ScreenStateConstraintDelegate(context: Context) :
    LiveStateConstraintDelegate<ScreenStateConstraintConfig>(context) {
    override val type: ConstraintType = ConstraintType.SCREEN_STATE

    override suspend fun evaluate(config: ScreenStateConstraintConfig): Boolean =
        deviceStateReader.isScreenInteractive() == config.on
}
