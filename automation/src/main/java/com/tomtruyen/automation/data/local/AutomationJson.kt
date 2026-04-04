package com.tomtruyen.automation.data.local

import com.tomtruyen.automation.features.actions.ActionType
import com.tomtruyen.automation.features.actions.config.ActionConfig
import com.tomtruyen.automation.features.actions.config.LogMessageActionConfig
import com.tomtruyen.automation.features.actions.config.ShowNotificationActionConfig
import com.tomtruyen.automation.core.utils.ComparisonOperator
import com.tomtruyen.automation.features.constraints.ConstraintType
import com.tomtruyen.automation.features.constraints.config.BatteryLevelConstraintConfig
import com.tomtruyen.automation.features.constraints.config.ConstraintConfig
import com.tomtruyen.automation.features.triggers.TriggerType
import com.tomtruyen.automation.core.model.BatteryChargeState
import com.tomtruyen.automation.features.triggers.config.BatteryChangedTriggerConfig
import com.tomtruyen.automation.features.triggers.config.TriggerConfig
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject

internal object AutomationJson {
    val instance = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
        classDiscriminator = "configType"
    }

    private val triggerListSerializer = ListSerializer(TriggerConfig.serializer())
    private val constraintListSerializer = ListSerializer(ConstraintConfig.serializer())
    private val actionListSerializer = ListSerializer(ActionConfig.serializer())

    fun encodeTriggers(triggers: List<TriggerConfig>): String =
        instance.encodeToString(triggerListSerializer, triggers)

    fun decodeTriggers(raw: String): List<TriggerConfig> =
        decode(raw, triggerListSerializer) { decodeLegacyTriggers(raw) }

    fun encodeConstraints(constraints: List<ConstraintConfig>): String =
        instance.encodeToString(constraintListSerializer, constraints)

    fun decodeConstraints(raw: String): List<ConstraintConfig> =
        decode(raw, constraintListSerializer) { decodeLegacyConstraints(raw) }

    fun encodeActions(actions: List<ActionConfig>): String =
        instance.encodeToString(actionListSerializer, actions)

    fun decodeActions(raw: String): List<ActionConfig> =
        decode(raw, actionListSerializer) { decodeLegacyActions(raw) }

    private fun <T> decode(
        raw: String,
        serializer: KSerializer<List<T>>,
        fallback: () -> List<T>
    ): List<T> {
        if (raw.isBlank()) return emptyList()
        return try {
            instance.decodeFromString(serializer, raw)
        } catch (_: SerializationException) {
            fallback()
        }
    }

    private fun decodeLegacyTriggers(raw: String): List<TriggerConfig> =
        parseLegacyItems(raw).mapNotNull { item ->
            when (item.type) {
                TriggerType.CHARGE_STATE.name -> BatteryChangedTriggerConfig(
                    state = when (item.values["state"]) {
                        "discharging" -> BatteryChargeState.DISCHARGING
                        "full" -> BatteryChargeState.FULL
                        "not_charging" -> BatteryChargeState.NOT_CHARGING
                        else -> BatteryChargeState.CHARGING
                    }
                )
                else -> null
            }
        }

    private fun decodeLegacyConstraints(raw: String): List<ConstraintConfig> =
        parseLegacyItems(raw).mapNotNull { item ->
            when (item.type) {
                ConstraintType.BATTERY_LEVEL.name -> BatteryLevelConstraintConfig(
                    operator = when (item.values["operator"]) {
                        "gt" -> ComparisonOperator.GREATER_THAN
                        "lt" -> ComparisonOperator.LESS_THAN
                        "lte" -> ComparisonOperator.LESS_THAN_OR_EQUAL
                        "eq" -> ComparisonOperator.EQUAL
                        "neq" -> ComparisonOperator.NOT_EQUAL
                        else -> ComparisonOperator.GREATER_THAN_OR_EQUAL
                    },
                    value = item.values["value"]?.toIntOrNull() ?: 80
                )
                else -> null
            }
        }

    private fun decodeLegacyActions(raw: String): List<ActionConfig> =
        parseLegacyItems(raw).mapNotNull { item ->
            when (item.type) {
                ActionType.SHOW_NOTIFICATION.name -> ShowNotificationActionConfig(
                    title = item.values["title"].orEmpty().ifBlank { "Automation started" },
                    message = item.values["message"].orEmpty().ifBlank { "Your rule was triggered." }
                )
                ActionType.LOG_MESSAGE.name -> LogMessageActionConfig(
                    message = item.values["message"].orEmpty().ifBlank { "Rule executed" }
                )
                else -> null
            }
        }

    private fun parseLegacyItems(raw: String): List<LegacyItem> =
        instance.parseToJsonElement(raw).jsonArray.mapNotNull { element ->
            val jsonObject = element.jsonObject
            val type = jsonObject["type"].asString() ?: return@mapNotNull null
            val valuesObject = jsonObject["values"] as? JsonObject
            val values = valuesObject?.mapValues { (_, value) -> value.asString().orEmpty() }.orEmpty()
            LegacyItem(type = type, values = values)
        }

    private fun kotlinx.serialization.json.JsonElement?.asString(): String? =
        (this as? JsonPrimitive)?.contentOrNull
}

private data class LegacyItem(
    val type: String,
    val values: Map<String, String>
)
