package com.tomtruyen.automation.core.definition

import androidx.annotation.StringRes
import com.tomtruyen.automation.core.config.AutomationConfig
import com.tomtruyen.automation.core.config.AutomationCategory
import com.tomtruyen.automation.core.permission.AutomationPermission
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

    val category: AutomationCategory
        get() = defaultConfig.category

    val fields: List<AutomationFieldDefinition>
    fun summarize(config: C, resolver: AutomationTextResolver): String

    fun cast(config: AutomationConfig<*>?): C? {
        if (config == null || !configClass.isInstance(config)) return null
        @Suppress("UNCHECKED_CAST")
        return config as C
    }
    fun initialConfig(): C = defaultConfig

    fun updateFieldAny(config: AutomationConfig<*>?, fieldId: String, value: String): C {
        val updated = fields.firstOrNull { it.id == fieldId }?.updateValue(config, value)
        return cast(updated) ?: cast(config) ?: defaultConfig
    }

    fun validate(config: C, resolver: AutomationTextResolver): List<String> =
        fields.flatMap { field -> field.validateInput(field.readValue(config), resolver) }

    fun validateAny(config: AutomationConfig<*>?, resolver: AutomationTextResolver): List<String> {
        val typedConfig = cast(config) ?: defaultConfig
        return validate(typedConfig, resolver)
    }

    fun summarizeAny(config: AutomationConfig<*>?, resolver: AutomationTextResolver): String {
        val typedConfig = cast(config) ?: defaultConfig
        return summarize(typedConfig, resolver)
    }
}
