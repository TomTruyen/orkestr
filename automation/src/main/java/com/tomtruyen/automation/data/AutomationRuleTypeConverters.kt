package com.tomtruyen.automation.data

import androidx.room.TypeConverter
import com.tomtruyen.automation.core.ConstraintGroup
import com.tomtruyen.automation.features.actions.config.ActionConfig
import com.tomtruyen.automation.features.constraints.config.ConstraintConfig
import com.tomtruyen.automation.features.triggers.config.TriggerConfig
import kotlinx.serialization.SerializationException
import kotlinx.serialization.builtins.ListSerializer

class AutomationRuleTypeConverters {
    @TypeConverter
    fun fromTriggers(value: List<TriggerConfig>): String =
        AutomationConfigJson.instance.encodeToString(ListSerializer(TriggerConfig.serializer()), value)

    @TypeConverter
    fun toTriggers(value: String): List<TriggerConfig> {
        if (value.isBlank()) return emptyList()
        return try {
            AutomationConfigJson.instance.decodeFromString(ListSerializer(TriggerConfig.serializer()), value)
        } catch (_: SerializationException) {
            emptyList()
        }
    }

    @TypeConverter
    fun fromConstraints(value: List<ConstraintConfig>): String =
        AutomationConfigJson.instance.encodeToString(ListSerializer(ConstraintConfig.serializer()), value)

    @TypeConverter
    fun toConstraints(value: String): List<ConstraintConfig> {
        if (value.isBlank()) return emptyList()
        return try {
            AutomationConfigJson.instance.decodeFromString(ListSerializer(ConstraintConfig.serializer()), value)
        } catch (_: SerializationException) {
            emptyList()
        }
    }

    @TypeConverter
    fun fromConstraintGroups(value: List<ConstraintGroup>): String =
        AutomationConfigJson.instance.encodeToString(ListSerializer(ConstraintGroup.serializer()), value)

    @TypeConverter
    fun toConstraintGroups(value: String): List<ConstraintGroup> {
        if (value.isBlank()) return emptyList()
        return try {
            AutomationConfigJson.instance.decodeFromString(ListSerializer(ConstraintGroup.serializer()), value)
        } catch (_: SerializationException) {
            emptyList()
        }
    }

    @TypeConverter
    fun fromActions(value: List<ActionConfig>): String =
        AutomationConfigJson.instance.encodeToString(ListSerializer(ActionConfig.serializer()), value)

    @TypeConverter
    fun toActions(value: String): List<ActionConfig> {
        if (value.isBlank()) return emptyList()
        return try {
            AutomationConfigJson.instance.decodeFromString(ListSerializer(ActionConfig.serializer()), value)
        } catch (_: SerializationException) {
            emptyList()
        }
    }
}
