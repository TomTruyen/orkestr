package com.tomtruyen.automation.features.constraints.delegate

import android.content.Context
import com.tomtruyen.automation.codegen.GenerateConstraintDelegate
import com.tomtruyen.automation.features.constraints.ConstraintType
import com.tomtruyen.automation.features.constraints.config.DateOfMonthConstraintConfig

@GenerateConstraintDelegate
class DateOfMonthConstraintDelegate(context: Context) :
    LiveStateConstraintDelegate<DateOfMonthConstraintConfig>(context) {
    override val type: ConstraintType = ConstraintType.DATE_OF_MONTH

    override suspend fun evaluate(config: DateOfMonthConstraintConfig): Boolean =
        deviceStateReader.currentDateTime().dayOfMonth in config.days
}
