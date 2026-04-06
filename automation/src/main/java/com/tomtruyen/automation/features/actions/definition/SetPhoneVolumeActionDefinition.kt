package com.tomtruyen.automation.features.actions.definition

import com.tomtruyen.automation.R
import com.tomtruyen.automation.codegen.GenerateActionDefinition
import com.tomtruyen.automation.core.definition.AutomationFieldType
import com.tomtruyen.automation.core.definition.AutomationOption
import com.tomtruyen.automation.core.definition.AutomationTextResolver
import com.tomtruyen.automation.core.definition.TypedAutomationFieldDefinition
import com.tomtruyen.automation.core.model.PhoneVolumeStream
import com.tomtruyen.automation.features.actions.config.SetPhoneVolumeActionConfig

@GenerateActionDefinition
object SetPhoneVolumeActionDefinition : ActionDefinition<SetPhoneVolumeActionConfig>(
    configClass = SetPhoneVolumeActionConfig::class,
    defaultConfig = SetPhoneVolumeActionConfig(),
) {
    override val titleRes = R.string.automation_definition_action_set_phone_volume_title
    override val descriptionRes = R.string.automation_definition_action_set_phone_volume_description
    override val fields = listOf(
        TypedAutomationFieldDefinition(
            configClass = SetPhoneVolumeActionConfig::class,
            defaultConfig = defaultConfig,
            id = FIELD_STREAM,
            labelRes = R.string.automation_definition_action_set_phone_volume_field_stream_label,
            type = AutomationFieldType.SINGLE_CHOICE,
            descriptionRes = R.string.automation_definition_action_set_phone_volume_field_stream_description,
            defaultValue = VALUE_MEDIA,
            options = listOf(
                AutomationOption(VALUE_MEDIA, R.string.automation_definition_action_set_phone_volume_option_media),
                AutomationOption(VALUE_RING, R.string.automation_definition_action_set_phone_volume_option_ring),
                AutomationOption(VALUE_CALL, R.string.automation_definition_action_set_phone_volume_option_call),
            ),
            reader = { it.stream.toFieldValue() },
            updater = { config, value -> config.copy(stream = value.toStream()) },
        ),
        TypedAutomationFieldDefinition(
            configClass = SetPhoneVolumeActionConfig::class,
            defaultConfig = defaultConfig,
            id = FIELD_LEVEL_PERCENT,
            labelRes = R.string.automation_definition_action_set_phone_volume_field_level_label,
            type = AutomationFieldType.NUMBER,
            descriptionRes = R.string.automation_definition_action_set_phone_volume_field_level_description,
            defaultValue = defaultConfig.levelPercent.toString(),
            placeholderRes = R.string.automation_definition_action_set_phone_volume_field_level_placeholder,
            reader = { it.levelPercent.toString() },
            updater = { config, value ->
                config.copy(
                    levelPercent = value.toIntOrNull() ?: defaultConfig.levelPercent,
                )
            },
        ),
    )

    override fun validate(config: SetPhoneVolumeActionConfig, resolver: AutomationTextResolver): List<String> =
        buildList {
            if (config.levelPercent !in MIN_LEVEL_PERCENT..MAX_LEVEL_PERCENT) {
                add(resolver.resolve(R.string.automation_definition_action_set_phone_volume_error_range))
            }
        }

    override fun summarize(config: SetPhoneVolumeActionConfig, resolver: AutomationTextResolver): String =
        resolver.resolve(
            R.string.automation_definition_action_set_phone_volume_summary,
            listOf(
                resolver.resolve(config.stream.toLabelRes()),
                config.levelPercent,
            ),
        )

    private fun PhoneVolumeStream.toFieldValue(): String = when (this) {
        PhoneVolumeStream.MEDIA -> VALUE_MEDIA
        PhoneVolumeStream.RING -> VALUE_RING
        PhoneVolumeStream.CALL -> VALUE_CALL
    }

    private fun String.toStream(): PhoneVolumeStream = when (this) {
        VALUE_RING -> PhoneVolumeStream.RING
        VALUE_CALL -> PhoneVolumeStream.CALL
        else -> PhoneVolumeStream.MEDIA
    }

    private fun PhoneVolumeStream.toLabelRes(): Int = when (this) {
        PhoneVolumeStream.MEDIA -> R.string.automation_definition_action_set_phone_volume_option_media
        PhoneVolumeStream.RING -> R.string.automation_definition_action_set_phone_volume_option_ring
        PhoneVolumeStream.CALL -> R.string.automation_definition_action_set_phone_volume_option_call
    }

    private const val FIELD_STREAM = "stream"
    private const val FIELD_LEVEL_PERCENT = "levelPercent"
    private const val VALUE_MEDIA = "media"
    private const val VALUE_RING = "ring"
    private const val VALUE_CALL = "call"
    private const val MIN_LEVEL_PERCENT = 0
    private const val MAX_LEVEL_PERCENT = 100
}
