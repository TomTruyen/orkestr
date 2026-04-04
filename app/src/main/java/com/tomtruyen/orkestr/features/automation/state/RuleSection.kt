package com.tomtruyen.orkestr.features.automation.state

import androidx.annotation.StringRes
import com.tomtruyen.orkestr.R

enum class RuleSection(
    @param:StringRes val titleRes: Int,
    @param:StringRes val helperRes: Int,
    @param:StringRes val singularTitleRes: Int
) {
    TRIGGERS(
        titleRes = R.string.automation_section_triggers_title,
        helperRes = R.string.automation_section_triggers_helper,
        singularTitleRes = R.string.automation_singular_trigger
    ),
    CONSTRAINTS(
        titleRes = R.string.automation_section_constraints_title,
        helperRes = R.string.automation_section_constraints_helper,
        singularTitleRes = R.string.automation_singular_constraint
    ),
    ACTIONS(
        titleRes = R.string.automation_section_actions_title,
        helperRes = R.string.automation_section_actions_helper,
        singularTitleRes = R.string.automation_singular_action
    )
}
