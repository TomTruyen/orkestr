package com.tomtruyen.automation.features.constraints.delegate

import android.content.Context
import com.tomtruyen.automation.codegen.GenerateConstraintDelegate
import com.tomtruyen.automation.core.model.MonthOfYear
import com.tomtruyen.automation.features.constraints.ConstraintType
import com.tomtruyen.automation.features.constraints.config.MonthOfYearConstraintConfig

@GenerateConstraintDelegate
class MonthOfYearConstraintDelegate(context: Context) :
    LiveStateConstraintDelegate<MonthOfYearConstraintConfig>(context) {
    override val type: ConstraintType = ConstraintType.MONTH_OF_YEAR

    override suspend fun evaluate(config: MonthOfYearConstraintConfig): Boolean =
        MonthOfYear.from(deviceStateReader.currentDateTime().month) in config.months
}
