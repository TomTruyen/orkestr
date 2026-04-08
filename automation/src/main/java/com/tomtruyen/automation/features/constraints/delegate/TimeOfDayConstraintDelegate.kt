package com.tomtruyen.automation.features.constraints.delegate

import android.content.Context
import com.tomtruyen.automation.codegen.GenerateConstraintDelegate
import com.tomtruyen.automation.features.constraints.ConstraintType
import com.tomtruyen.automation.features.constraints.config.TimeOfDayConstraintConfig

@GenerateConstraintDelegate
class TimeOfDayConstraintDelegate(context: Context) : LiveStateConstraintDelegate<TimeOfDayConstraintConfig>(context) {
    override val type: ConstraintType = ConstraintType.TIME_OF_DAY

    override suspend fun evaluate(config: TimeOfDayConstraintConfig): Boolean =
        config.matches(deviceStateReader.currentDateTime().toLocalTime())
}
