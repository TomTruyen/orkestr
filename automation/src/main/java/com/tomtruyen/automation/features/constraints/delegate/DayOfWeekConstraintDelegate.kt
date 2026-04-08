package com.tomtruyen.automation.features.constraints.delegate

import android.content.Context
import com.tomtruyen.automation.codegen.GenerateConstraintDelegate
import com.tomtruyen.automation.core.model.Weekday
import com.tomtruyen.automation.features.constraints.ConstraintType
import com.tomtruyen.automation.features.constraints.config.DayOfWeekConstraintConfig

@GenerateConstraintDelegate
class DayOfWeekConstraintDelegate(context: Context) : LiveStateConstraintDelegate<DayOfWeekConstraintConfig>(context) {
    override val type: ConstraintType = ConstraintType.DAY_OF_WEEK

    override suspend fun evaluate(config: DayOfWeekConstraintConfig): Boolean =
        Weekday.from(deviceStateReader.currentDateTime().dayOfWeek) in config.days
}
