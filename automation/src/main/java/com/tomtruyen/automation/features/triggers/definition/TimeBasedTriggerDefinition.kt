package com.tomtruyen.automation.features.triggers.definition

import com.tomtruyen.automation.R
import com.tomtruyen.automation.codegen.GenerateTriggerDefinition
import com.tomtruyen.automation.core.definition.AutomationFieldType
import com.tomtruyen.automation.core.definition.AutomationTextResolver
import com.tomtruyen.automation.core.definition.TypedAutomationFieldDefinition
import com.tomtruyen.automation.core.model.Weekday
import com.tomtruyen.automation.features.triggers.config.TimeBasedTriggerConfig

@GenerateTriggerDefinition
object TimeBasedTriggerDefinition : TriggerDefinition<TimeBasedTriggerConfig>(
    configClass = TimeBasedTriggerConfig::class,
    defaultConfig = TimeBasedTriggerConfig(),
) {
    override val titleRes = R.string.automation_definition_trigger_time_based_title
    override val descriptionRes = R.string.automation_definition_trigger_time_based_description
    override val fields = listOf(
        TypedAutomationFieldDefinition(
            configClass = TimeBasedTriggerConfig::class,
            defaultConfig = defaultConfig,
            id = FIELD_HOUR,
            labelRes = R.string.automation_definition_trigger_time_based_field_hour_label,
            type = AutomationFieldType.NUMBER,
            descriptionRes = R.string.automation_definition_trigger_time_based_field_hour_description,
            defaultValue = defaultConfig.hour.toString(),
            reader = { it.hour.toString() },
            updater = { config, value -> config.copy(hour = value.toIntOrNull() ?: defaultConfig.hour) },
            inputValidator = { input, resolver ->
                if ((input.toIntOrNull() ?: -1) in 0..23) {
                    emptyList()
                } else {
                    listOf(resolver.resolve(R.string.automation_definition_trigger_time_based_error_hour_range))
                }
            },
        ),
        TypedAutomationFieldDefinition(
            configClass = TimeBasedTriggerConfig::class,
            defaultConfig = defaultConfig,
            id = FIELD_MINUTE,
            labelRes = R.string.automation_definition_trigger_time_based_field_minute_label,
            type = AutomationFieldType.NUMBER,
            descriptionRes = R.string.automation_definition_trigger_time_based_field_minute_description,
            defaultValue = defaultConfig.minute.toString(),
            reader = { it.minute.toString() },
            updater = { config, value -> config.copy(minute = value.toIntOrNull() ?: defaultConfig.minute) },
            inputValidator = { input, resolver ->
                if ((input.toIntOrNull() ?: -1) in 0..59) {
                    emptyList()
                } else {
                    listOf(resolver.resolve(R.string.automation_definition_trigger_time_based_error_minute_range))
                }
            },
        ),
    ) + Weekday.entries.map { day ->
        TypedAutomationFieldDefinition(
            configClass = TimeBasedTriggerConfig::class,
            defaultConfig = defaultConfig,
            id = "day_${day.name.lowercase()}",
            labelRes = day.labelRes,
            type = AutomationFieldType.BOOLEAN,
            descriptionRes = R.string.automation_definition_trigger_time_based_field_day_description,
            defaultValue = true.toString(),
            reader = { config -> (day in config.days).toString() },
            updater = { config, value ->
                val enabled = value.toBoolean()
                config.copy(
                    days = config.days.toMutableSet().apply {
                        if (enabled) add(day) else remove(day)
                    },
                )
            },
        )
    }

    override fun validate(config: TimeBasedTriggerConfig, resolver: AutomationTextResolver): List<String> {
        val errors = super.validate(config, resolver).toMutableList()
        if (config.days.isEmpty()) {
            errors += resolver.resolve(R.string.automation_definition_trigger_time_based_error_days_required)
        }
        return errors
    }

    override fun summarize(config: TimeBasedTriggerConfig, resolver: AutomationTextResolver): String = resolver.resolve(
        R.string.automation_definition_trigger_time_based_summary,
        listOf(
            "%02d:%02d".format(config.hour, config.minute),
            config.days.sortedBy(Weekday::ordinal).joinToString(", ") { resolver.resolve(it.labelRes) },
        ),
    )

    private val Weekday.labelRes: Int
        get() = when (this) {
            Weekday.MONDAY -> R.string.automation_definition_weekday_monday
            Weekday.TUESDAY -> R.string.automation_definition_weekday_tuesday
            Weekday.WEDNESDAY -> R.string.automation_definition_weekday_wednesday
            Weekday.THURSDAY -> R.string.automation_definition_weekday_thursday
            Weekday.FRIDAY -> R.string.automation_definition_weekday_friday
            Weekday.SATURDAY -> R.string.automation_definition_weekday_saturday
            Weekday.SUNDAY -> R.string.automation_definition_weekday_sunday
        }

    private const val FIELD_HOUR = "hour"
    private const val FIELD_MINUTE = "minute"
}
