package com.tomtruyen.automation.features.actions.config

import com.tomtruyen.automation.core.AutomationLogSeverity

enum class LogMessageSeverity(val logSeverity: AutomationLogSeverity) {
    DEBUG(AutomationLogSeverity.DEBUG),
    INFO(AutomationLogSeverity.INFO),
    WARNING(AutomationLogSeverity.WARNING),
    ERROR(AutomationLogSeverity.ERROR),
}
