package com.tomtruyen.automation.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "automation_rules")
data class AutomationRuleEntity(
    @PrimaryKey val id: String,
    val name: String,
    val enabled: Boolean,
    val triggersJson: String,
    val constraintsJson: String,
    val actionsJson: String,
    val updatedAtEpochMillis: Long
)