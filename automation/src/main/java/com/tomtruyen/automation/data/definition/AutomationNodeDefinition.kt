package com.tomtruyen.automation.data.definition

import androidx.annotation.StringRes
import com.tomtruyen.automation.core.AutomationConfig
import com.tomtruyen.automation.core.permission.AutomationPermission
import com.tomtruyen.automation.features.actions.ActionType
import com.tomtruyen.automation.features.actions.config.ActionConfig
import com.tomtruyen.automation.features.constraints.ConstraintType
import com.tomtruyen.automation.features.constraints.config.ConstraintConfig
import com.tomtruyen.automation.features.triggers.TriggerType
import com.tomtruyen.automation.features.triggers.config.TriggerConfig
import kotlin.reflect.KClass

interface AutomationNodeDefinition<C : AutomationConfig<T>, T : Enum<T>> {
    val configClass: KClass<C>
    val defaultConfig: C
    val key: String
        get() = type.name
    val type: T
        get() = defaultConfig.type
    val requiredPermissions: List<AutomationPermission>
        get() = defaultConfig.requiredPermissions

    @get:StringRes
    val titleRes: Int

    @get:StringRes
    val descriptionRes: Int

    val fields: List<AutomationFieldDefinition>

    fun updateField(config: C, fieldId: String, value: String): C
    fun valuesOf(config: C): Map<String, String>
    fun summarize(config: C, resolver: AutomationTextResolver): String
    fun validateValues(values: Map<String, String>, resolver: AutomationTextResolver): List<String> =
        validateFields(fields, values, resolver)

    fun validate(config: C, resolver: AutomationTextResolver): List<String> =
        validateValues(valuesOf(config), resolver)

    fun cast(config: AutomationConfig<*>?): C? {
        if (config == null || !configClass.isInstance(config)) return null
        @Suppress("UNCHECKED_CAST")
        return config as C
    }
    fun initialConfig(): C = defaultConfig

    fun updateFieldAny(config: AutomationConfig<*>?, fieldId: String, value: String): C {
        val typedConfig = cast(config) ?: defaultConfig
        return updateField(typedConfig, fieldId, value)
    }

    fun valuesOfAny(config: AutomationConfig<*>?): Map<String, String> {
        val typedConfig = cast(config) ?: defaultConfig
        return valuesOf(typedConfig)
    }

    fun summarizeAny(config: AutomationConfig<*>?, resolver: AutomationTextResolver): String {
        val typedConfig = cast(config) ?: defaultConfig
        return summarize(typedConfig, resolver)
    }

    fun validateAny(config: AutomationConfig<*>?, resolver: AutomationTextResolver): List<String> {
        val typedConfig = cast(config) ?: defaultConfig
        return validate(typedConfig, resolver)
    }

    fun validateValuesAny(values: Map<String, String>, resolver: AutomationTextResolver): List<String> =
        validateValues(values, resolver)
}

abstract class TriggerDefinition<C : TriggerConfig>(
    final override val configClass: KClass<C>,
    final override val defaultConfig: C
) : AutomationNodeDefinition<C, TriggerType>

abstract class ConstraintDefinition<C : ConstraintConfig>(
    final override val configClass: KClass<C>,
    final override val defaultConfig: C
) : AutomationNodeDefinition<C, ConstraintType>

abstract class ActionDefinition<C : ActionConfig>(
    final override val configClass: KClass<C>,
    final override val defaultConfig: C
) : AutomationNodeDefinition<C, ActionType>
