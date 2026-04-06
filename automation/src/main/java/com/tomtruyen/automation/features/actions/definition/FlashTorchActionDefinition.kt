package com.tomtruyen.automation.features.actions.definition

import com.tomtruyen.automation.R
import com.tomtruyen.automation.codegen.GenerateActionDefinition
import com.tomtruyen.automation.core.definition.AutomationFieldType
import com.tomtruyen.automation.core.definition.AutomationTextResolver
import com.tomtruyen.automation.core.definition.TypedAutomationFieldDefinition
import com.tomtruyen.automation.features.actions.config.FlashTorchActionConfig

@GenerateActionDefinition
object FlashTorchActionDefinition : ActionDefinition<FlashTorchActionConfig>(
    configClass = FlashTorchActionConfig::class,
    defaultConfig = FlashTorchActionConfig(),
) {
    override val titleRes = R.string.automation_definition_action_flash_torch_title
    override val descriptionRes = R.string.automation_definition_action_flash_torch_description
    override val fields = listOf(
        numberField(
            id = FIELD_PULSE_COUNT,
            labelRes = R.string.automation_definition_action_flash_torch_field_pulse_count_label,
            descriptionRes = R.string.automation_definition_action_flash_torch_field_pulse_count_description,
            defaultValue = defaultConfig.pulseCount.toString(),
            placeholderRes = R.string.automation_definition_action_flash_torch_field_pulse_count_placeholder,
            reader = { it.pulseCount.toString() },
            updater = { config, value -> config.copy(pulseCount = value.toIntOrNull() ?: defaultConfig.pulseCount) },
        ),
        numberField(
            id = FIELD_ON_DURATION,
            labelRes = R.string.automation_definition_action_flash_torch_field_on_duration_label,
            descriptionRes = R.string.automation_definition_action_flash_torch_field_on_duration_description,
            defaultValue = defaultConfig.onDurationMillis.toString(),
            placeholderRes = R.string.automation_definition_action_flash_torch_field_on_duration_placeholder,
            reader = { it.onDurationMillis.toString() },
            updater = { config, value -> config.copy(onDurationMillis = value.toIntOrNull() ?: defaultConfig.onDurationMillis) },
        ),
        numberField(
            id = FIELD_OFF_DURATION,
            labelRes = R.string.automation_definition_action_flash_torch_field_off_duration_label,
            descriptionRes = R.string.automation_definition_action_flash_torch_field_off_duration_description,
            defaultValue = defaultConfig.offDurationMillis.toString(),
            placeholderRes = R.string.automation_definition_action_flash_torch_field_off_duration_placeholder,
            reader = { it.offDurationMillis.toString() },
            updater = { config, value -> config.copy(offDurationMillis = value.toIntOrNull() ?: defaultConfig.offDurationMillis) },
        ),
    )

    override fun validate(config: FlashTorchActionConfig, resolver: AutomationTextResolver): List<String> = buildList {
        if (config.pulseCount !in 1..10) {
            add(resolver.resolve(R.string.automation_definition_action_flash_torch_error_pulse_count_range))
        }
        if (config.onDurationMillis !in 50..5_000) {
            add(resolver.resolve(R.string.automation_definition_action_flash_torch_error_duration_range))
        }
        if (config.offDurationMillis !in 50..5_000) {
            add(resolver.resolve(R.string.automation_definition_action_flash_torch_error_duration_range))
        }
    }

    override fun summarize(config: FlashTorchActionConfig, resolver: AutomationTextResolver): String = resolver.resolve(
        R.string.automation_definition_action_flash_torch_summary,
        listOf(config.pulseCount),
    )

    private fun numberField(
        id: String,
        labelRes: Int,
        descriptionRes: Int,
        defaultValue: String,
        placeholderRes: Int,
        reader: (FlashTorchActionConfig) -> String,
        updater: (FlashTorchActionConfig, String) -> FlashTorchActionConfig,
    ) = TypedAutomationFieldDefinition(
        configClass = FlashTorchActionConfig::class,
        defaultConfig = defaultConfig,
        id = id,
        labelRes = labelRes,
        type = AutomationFieldType.NUMBER,
        descriptionRes = descriptionRes,
        defaultValue = defaultValue,
        placeholderRes = placeholderRes,
        reader = reader,
        updater = updater,
    )

    private const val FIELD_PULSE_COUNT = "pulseCount"
    private const val FIELD_ON_DURATION = "onDurationMillis"
    private const val FIELD_OFF_DURATION = "offDurationMillis"
}
