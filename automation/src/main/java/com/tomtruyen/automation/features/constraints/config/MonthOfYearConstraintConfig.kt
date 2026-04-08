package com.tomtruyen.automation.features.constraints.config

import com.tomtruyen.automation.core.config.AutomationCategory
import com.tomtruyen.automation.core.model.MonthOfYear
import com.tomtruyen.automation.features.constraints.ConstraintType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName(MonthOfYearConstraintConfig.DISCRIMINATOR)
data class MonthOfYearConstraintConfig(val months: Set<MonthOfYear> = setOf(MonthOfYear.JANUARY)) : ConstraintConfig {
    override val type: ConstraintType = ConstraintType.MONTH_OF_YEAR
    override val category: AutomationCategory = AutomationCategory.UTILITY

    companion object {
        const val DISCRIMINATOR = "month_of_year_constraint"
    }
}
