package com.tomtruyen.automation.core.config

import androidx.annotation.StringRes
import com.tomtruyen.automation.R

enum class AutomationCategory(@param:StringRes val titleRes: Int) {
    BATTERY_POWER(R.string.automation_definition_category_battery_power),
    LOCATION(R.string.automation_definition_category_location),
    NOTIFICATIONS(R.string.automation_definition_category_notifications),
    VOLUME(R.string.automation_definition_category_volume),
    UTILITY(R.string.automation_definition_category_utility),
}
