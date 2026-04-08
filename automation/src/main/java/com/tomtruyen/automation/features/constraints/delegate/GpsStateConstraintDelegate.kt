package com.tomtruyen.automation.features.constraints.delegate

import android.content.Context
import com.tomtruyen.automation.codegen.GenerateConstraintDelegate
import com.tomtruyen.automation.features.constraints.ConstraintType
import com.tomtruyen.automation.features.constraints.config.GpsStateConstraintConfig

@GenerateConstraintDelegate
class GpsStateConstraintDelegate(context: Context) : LiveStateConstraintDelegate<GpsStateConstraintConfig>(context) {
    override val type: ConstraintType = ConstraintType.GPS_STATE

    override suspend fun evaluate(config: GpsStateConstraintConfig): Boolean =
        deviceStateReader.isGpsEnabled() == config.enabled
}
