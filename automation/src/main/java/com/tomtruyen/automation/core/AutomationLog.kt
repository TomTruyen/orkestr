package com.tomtruyen.automation.core

data class AutomationLog(
    val id: Long,
    val timestampEpochMillis: Long,
    val severity: AutomationLogSeverity,
    val message: String,
    val stackTrace: String? = null,
)

enum class AutomationLogSeverity {
    DEBUG,
    INFO,
    WARNING,
    ERROR,
}
