package com.tomtruyen.automation.data.local

import com.tomtruyen.automation.features.actions.config.ActionConfig
import com.tomtruyen.automation.features.constraints.config.ConstraintConfig
import com.tomtruyen.automation.features.triggers.config.TriggerConfig
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

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
        decode(raw, triggerListSerializer)

    fun encodeConstraints(constraints: List<ConstraintConfig>): String =
        instance.encodeToString(constraintListSerializer, constraints)

    fun decodeConstraints(raw: String): List<ConstraintConfig> =
        decode(raw, constraintListSerializer)

    fun encodeActions(actions: List<ActionConfig>): String =
        instance.encodeToString(actionListSerializer, actions)

    fun decodeActions(raw: String): List<ActionConfig> =
        decode(raw, actionListSerializer)

    private fun <T> decode(
        raw: String,
        serializer: KSerializer<List<T>>,
        fallback: (() -> List<T>)? = null
    ): List<T> {
        if (raw.isBlank()) return emptyList()
        return try {
            instance.decodeFromString(serializer, raw)
        } catch (_: SerializationException) {
            fallback?.invoke()
        } ?: emptyList()
    }
}