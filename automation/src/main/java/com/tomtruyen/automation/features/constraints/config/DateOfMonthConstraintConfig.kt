package com.tomtruyen.automation.features.constraints.config

import com.tomtruyen.automation.core.config.AutomationCategory
import com.tomtruyen.automation.features.constraints.ConstraintType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName(DateOfMonthConstraintConfig.DISCRIMINATOR)
data class DateOfMonthConstraintConfig(val days: Set<Int> = setOf(1)) : ConstraintConfig {
    override val type: ConstraintType = ConstraintType.DATE_OF_MONTH
    override val category: AutomationCategory = AutomationCategory.UTILITY

    companion object {
        const val DISCRIMINATOR = "date_of_month_constraint"
    }
}
