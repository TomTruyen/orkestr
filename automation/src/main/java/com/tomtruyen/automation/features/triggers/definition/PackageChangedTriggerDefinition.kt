package com.tomtruyen.automation.features.triggers.definition

import com.tomtruyen.automation.R
import com.tomtruyen.automation.codegen.GenerateTriggerDefinition
import com.tomtruyen.automation.core.definition.AutomationFieldType
import com.tomtruyen.automation.core.definition.AutomationOption
import com.tomtruyen.automation.core.definition.AutomationTextResolver
import com.tomtruyen.automation.core.definition.TypedAutomationFieldDefinition
import com.tomtruyen.automation.core.model.PackageChangeType
import com.tomtruyen.automation.features.triggers.config.PackageChangedTriggerConfig

@GenerateTriggerDefinition
object PackageChangedTriggerDefinition : TriggerDefinition<PackageChangedTriggerConfig>(
    configClass = PackageChangedTriggerConfig::class,
    defaultConfig = PackageChangedTriggerConfig(),
) {
    override val titleRes = R.string.automation_definition_trigger_package_changed_title
    override val descriptionRes = R.string.automation_definition_trigger_package_changed_description
    override val fields = listOf(
        TypedAutomationFieldDefinition(
            configClass = PackageChangedTriggerConfig::class,
            defaultConfig = defaultConfig,
            id = FIELD_CHANGE_TYPE,
            labelRes = R.string.automation_definition_trigger_package_changed_field_change_type_label,
            type = AutomationFieldType.SINGLE_CHOICE,
            descriptionRes = R.string.automation_definition_trigger_package_changed_field_change_type_description,
            defaultValue = VALUE_INSTALLED,
            options = listOf(
                AutomationOption(
                    VALUE_INSTALLED,
                    R.string.automation_definition_trigger_package_changed_option_installed,
                ),
                AutomationOption(VALUE_REMOVED, R.string.automation_definition_trigger_package_changed_option_removed),
                AutomationOption(VALUE_UPDATED, R.string.automation_definition_trigger_package_changed_option_updated),
            ),
            reader = { it.changeType.toFieldValue() },
            updater = { config, value -> config.copy(changeType = value.toPackageChangeType()) },
        ),
        TypedAutomationFieldDefinition(
            configClass = PackageChangedTriggerConfig::class,
            defaultConfig = defaultConfig,
            id = FIELD_PACKAGE_NAME,
            labelRes = R.string.automation_definition_trigger_package_changed_field_package_label,
            type = AutomationFieldType.TEXT,
            descriptionRes = R.string.automation_definition_trigger_package_changed_field_package_description,
            required = false,
            placeholderRes = R.string.automation_definition_trigger_package_changed_field_package_placeholder,
            reader = { it.packageName },
            updater = { config, value -> config.copy(packageName = value.trim()) },
        ),
    )

    override fun summarize(config: PackageChangedTriggerConfig, resolver: AutomationTextResolver): String =
        resolver.resolve(
            R.string.automation_definition_trigger_package_changed_summary,
            listOf(
                config.packageName.ifBlank {
                    resolver.resolve(R.string.automation_definition_trigger_package_changed_any_app)
                },
                resolver.resolve(config.changeType.toLabelRes()),
            ),
        )

    private fun PackageChangeType.toFieldValue(): String = when (this) {
        PackageChangeType.INSTALLED -> VALUE_INSTALLED
        PackageChangeType.REMOVED -> VALUE_REMOVED
        PackageChangeType.UPDATED -> VALUE_UPDATED
    }

    private fun String.toPackageChangeType(): PackageChangeType = when (this) {
        VALUE_REMOVED -> PackageChangeType.REMOVED
        VALUE_UPDATED -> PackageChangeType.UPDATED
        else -> PackageChangeType.INSTALLED
    }

    private fun PackageChangeType.toLabelRes(): Int = when (this) {
        PackageChangeType.INSTALLED -> R.string.automation_definition_trigger_package_changed_option_installed
        PackageChangeType.REMOVED -> R.string.automation_definition_trigger_package_changed_option_removed
        PackageChangeType.UPDATED -> R.string.automation_definition_trigger_package_changed_option_updated
    }

    private const val FIELD_CHANGE_TYPE = "changeType"
    private const val FIELD_PACKAGE_NAME = "packageName"
    private const val VALUE_INSTALLED = "installed"
    private const val VALUE_REMOVED = "removed"
    private const val VALUE_UPDATED = "updated"
}
