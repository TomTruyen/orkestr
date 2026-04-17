package com.tomtruyen.automation.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tomtruyen.automation.features.actions.config.ActionConfig
import com.tomtruyen.automation.features.constraints.config.ConstraintConfig
import com.tomtruyen.automation.features.triggers.config.TriggerConfig

@Entity(tableName = "automation_node_groups")
data class AutomationNodeGroupEntity(
    @PrimaryKey val id: String,
    val name: String,
    val type: String,
    @ColumnInfo(name = "triggersJson") val triggers: List<TriggerConfig>,
    @ColumnInfo(name = "constraintsJson") val constraints: List<ConstraintConfig>,
    @ColumnInfo(name = "actionsJson") val actions: List<ActionConfig>,
    val updatedAtEpochMillis: Long,
)
