package com.tomtruyen.automation.features.constraints.delegate

import android.content.Context
import com.tomtruyen.automation.codegen.GenerateConstraintDelegate
import com.tomtruyen.automation.features.constraints.ConstraintType
import com.tomtruyen.automation.features.constraints.config.MobileDataStateConstraintConfig

@GenerateConstraintDelegate
class MobileDataStateConstraintDelegate(context: Context) :
    LiveStateConstraintDelegate<MobileDataStateConstraintConfig>(context) {
    override val type: ConstraintType = ConstraintType.MOBILE_DATA_STATE

    override suspend fun evaluate(config: MobileDataStateConstraintConfig): Boolean =
        deviceStateReader.isMobileDataEnabled() == config.enabled
}
