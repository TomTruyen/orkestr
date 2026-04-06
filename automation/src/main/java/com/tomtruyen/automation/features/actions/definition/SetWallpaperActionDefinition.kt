package com.tomtruyen.automation.features.actions.definition

import com.tomtruyen.automation.R
import com.tomtruyen.automation.codegen.GenerateActionDefinition
import com.tomtruyen.automation.core.definition.AutomationFieldType
import com.tomtruyen.automation.core.definition.AutomationOption
import com.tomtruyen.automation.core.definition.AutomationTextResolver
import com.tomtruyen.automation.core.definition.TypedAutomationFieldDefinition
import com.tomtruyen.automation.core.model.WallpaperTarget
import com.tomtruyen.automation.features.actions.config.SetWallpaperActionConfig

@GenerateActionDefinition
object SetWallpaperActionDefinition : ActionDefinition<SetWallpaperActionConfig>(
    configClass = SetWallpaperActionConfig::class,
    defaultConfig = SetWallpaperActionConfig(),
) {
    override val titleRes = R.string.automation_definition_action_set_wallpaper_title
    override val descriptionRes = R.string.automation_definition_action_set_wallpaper_description
    override val fields = listOf(
        TypedAutomationFieldDefinition(
            configClass = SetWallpaperActionConfig::class,
            defaultConfig = defaultConfig,
            id = FIELD_TARGET,
            labelRes = R.string.automation_definition_action_set_wallpaper_field_target_label,
            type = AutomationFieldType.SINGLE_CHOICE,
            descriptionRes = R.string.automation_definition_action_set_wallpaper_field_target_description,
            defaultValue = VALUE_HOME_AND_LOCK,
            options = listOf(
                AutomationOption(VALUE_HOME, R.string.automation_definition_action_set_wallpaper_option_home),
                AutomationOption(VALUE_LOCK, R.string.automation_definition_action_set_wallpaper_option_lock),
                AutomationOption(
                    VALUE_HOME_AND_LOCK,
                    R.string.automation_definition_action_set_wallpaper_option_home_and_lock,
                ),
            ),
            reader = { it.target.toFieldValue() },
            updater = { config, value -> config.copy(target = value.toTarget()) },
        ),
    )

    override fun validate(config: SetWallpaperActionConfig, resolver: AutomationTextResolver): List<String> =
        buildList {
            if (config.imageUri.isBlank()) {
                add(resolver.resolve(R.string.automation_definition_action_set_wallpaper_error_missing_image))
            }
        }

    override fun summarize(config: SetWallpaperActionConfig, resolver: AutomationTextResolver): String =
        resolver.resolve(
            R.string.automation_definition_action_set_wallpaper_summary,
            listOf(
                config.imageLabel.ifBlank {
                    resolver.resolve(
                        R.string.automation_definition_action_set_wallpaper_unselected,
                    )
                },
                resolver.resolve(config.target.toLabelRes()),
            ),
        )

    private fun WallpaperTarget.toFieldValue(): String = when (this) {
        WallpaperTarget.HOME_SCREEN -> VALUE_HOME
        WallpaperTarget.LOCK_SCREEN -> VALUE_LOCK
        WallpaperTarget.HOME_AND_LOCK_SCREEN -> VALUE_HOME_AND_LOCK
    }

    private fun String.toTarget(): WallpaperTarget = when (this) {
        VALUE_HOME -> WallpaperTarget.HOME_SCREEN
        VALUE_LOCK -> WallpaperTarget.LOCK_SCREEN
        else -> WallpaperTarget.HOME_AND_LOCK_SCREEN
    }

    private fun WallpaperTarget.toLabelRes(): Int = when (this) {
        WallpaperTarget.HOME_SCREEN -> R.string.automation_definition_action_set_wallpaper_option_home
        WallpaperTarget.LOCK_SCREEN -> R.string.automation_definition_action_set_wallpaper_option_lock
        WallpaperTarget.HOME_AND_LOCK_SCREEN -> R.string.automation_definition_action_set_wallpaper_option_home_and_lock
    }

    private const val FIELD_TARGET = "target"
    private const val VALUE_HOME = "home"
    private const val VALUE_LOCK = "lock"
    private const val VALUE_HOME_AND_LOCK = "home_and_lock"
}
