package com.tomtruyen.orkestr.features.automation.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tomtruyen.automation.core.config.AutomationConfig
import com.tomtruyen.automation.core.definition.AutomationNodeDefinition
import com.tomtruyen.automation.core.model.AppLifecycleTransitionType
import com.tomtruyen.automation.core.model.AutomationLocalTime
import com.tomtruyen.automation.core.model.BatteryChargeState
import com.tomtruyen.automation.core.model.DoNotDisturbMode
import com.tomtruyen.automation.core.model.GeofenceTransitionType
import com.tomtruyen.automation.core.model.GeofenceUpdateRate
import com.tomtruyen.automation.core.model.MonthOfYear
import com.tomtruyen.automation.core.model.PhoneVolumeStream
import com.tomtruyen.automation.core.model.PowerConnectionState
import com.tomtruyen.automation.core.model.WallpaperTarget
import com.tomtruyen.automation.core.model.Weekday
import com.tomtruyen.automation.core.utils.ComparisonOperator
import com.tomtruyen.automation.features.actions.ActionType
import com.tomtruyen.automation.features.actions.config.DoNotDisturbActionConfig
import com.tomtruyen.automation.features.actions.config.FlashTorchActionConfig
import com.tomtruyen.automation.features.actions.config.ForceLocationUpdateActionConfig
import com.tomtruyen.automation.features.actions.config.LaunchApplicationActionConfig
import com.tomtruyen.automation.features.actions.config.LogMessageActionConfig
import com.tomtruyen.automation.features.actions.config.OpenWebsiteActionConfig
import com.tomtruyen.automation.features.actions.config.SetPhoneVibrateActionConfig
import com.tomtruyen.automation.features.actions.config.SetPhoneVolumeActionConfig
import com.tomtruyen.automation.features.actions.config.SetWallpaperActionConfig
import com.tomtruyen.automation.features.actions.config.ShowNotificationActionConfig
import com.tomtruyen.automation.features.actions.config.VibratePhoneActionConfig
import com.tomtruyen.automation.features.actions.definition.ActionDefinition
import com.tomtruyen.automation.features.constraints.ConstraintType
import com.tomtruyen.automation.features.constraints.config.BatteryLevelConstraintConfig
import com.tomtruyen.automation.features.constraints.config.BatterySaverStateConstraintConfig
import com.tomtruyen.automation.features.constraints.config.BluetoothStateConstraintConfig
import com.tomtruyen.automation.features.constraints.config.CallStateConstraintConfig
import com.tomtruyen.automation.features.constraints.config.DateOfMonthConstraintConfig
import com.tomtruyen.automation.features.constraints.config.DayOfWeekConstraintConfig
import com.tomtruyen.automation.features.constraints.config.GeofenceConstraintConfig
import com.tomtruyen.automation.features.constraints.config.GpsStateConstraintConfig
import com.tomtruyen.automation.features.constraints.config.HeadphoneConnectionConstraintConfig
import com.tomtruyen.automation.features.constraints.config.MobileDataStateConstraintConfig
import com.tomtruyen.automation.features.constraints.config.MonthOfYearConstraintConfig
import com.tomtruyen.automation.features.constraints.config.MusicActiveConstraintConfig
import com.tomtruyen.automation.features.constraints.config.PowerConnectedConstraintConfig
import com.tomtruyen.automation.features.constraints.config.ScreenStateConstraintConfig
import com.tomtruyen.automation.features.constraints.config.TimeOfDayConstraintConfig
import com.tomtruyen.automation.features.constraints.config.WifiSsidConstraintConfig
import com.tomtruyen.automation.features.constraints.config.WifiStateConstraintConfig
import com.tomtruyen.automation.features.constraints.definition.ConstraintDefinition
import com.tomtruyen.automation.features.triggers.TriggerType
import com.tomtruyen.automation.features.triggers.config.ApplicationLifecycleTriggerConfig
import com.tomtruyen.automation.features.triggers.config.BatteryChangedTriggerConfig
import com.tomtruyen.automation.features.triggers.config.BatteryLevelTriggerConfig
import com.tomtruyen.automation.features.triggers.config.BatterySaverStateTriggerConfig
import com.tomtruyen.automation.features.triggers.config.GeofenceTriggerConfig
import com.tomtruyen.automation.features.triggers.config.NotificationReceivedTriggerConfig
import com.tomtruyen.automation.features.triggers.config.PowerConnectionTriggerConfig
import com.tomtruyen.automation.features.triggers.config.TimeBasedTriggerConfig
import com.tomtruyen.automation.features.triggers.config.WifiSsidTriggerConfig
import com.tomtruyen.automation.features.triggers.definition.TriggerDefinition
import com.tomtruyen.automation.generated.GeneratedActionProvider
import com.tomtruyen.automation.generated.GeneratedConstraintProvider
import com.tomtruyen.automation.generated.GeneratedTriggerProvider
import com.tomtruyen.orkestr.common.component.AutomationBetaChip
import com.tomtruyen.orkestr.common.component.AutomationCardColumn
import com.tomtruyen.orkestr.common.component.AutomationRequiredSdkChip

internal object AutomationGeneratedDefinitionPreviewCatalog {
    internal val sampleMonthDays = setOf(1, 15, 28)

    val triggerTypes: Set<TriggerType> = GeneratedTriggerProvider.definitions.mapTo(linkedSetOf()) { it.type }
    val constraintTypes: Set<ConstraintType> = GeneratedConstraintProvider.definitions.mapTo(linkedSetOf()) { it.type }
    val actionTypes: Set<ActionType> = GeneratedActionProvider.definitions.mapTo(linkedSetOf()) { it.type }

    fun trigger(type: TriggerType): TriggerDefinition<*> =
        GeneratedTriggerProvider.definitions.first { it.type == type }

    fun constraint(type: ConstraintType): ConstraintDefinition<*> =
        GeneratedConstraintProvider.definitions.first { it.type == type }

    fun action(type: ActionType): ActionDefinition<*> = GeneratedActionProvider.definitions.first { it.type == type }
}

@Preview(showBackground = true, widthDp = 420)
@Composable
internal fun ChargeStateTriggerDefinitionComposePreview() {
    AutomationDefinitionPreviewCard(
        definition = AutomationGeneratedDefinitionPreviewCatalog.trigger(TriggerType.CHARGE_STATE),
        config = BatteryChangedTriggerConfig(state = BatteryChargeState.FULL),
    )
}

@Preview(showBackground = true, widthDp = 420)
@Composable
internal fun TimeBasedTriggerDefinitionComposePreview() {
    AutomationDefinitionPreviewCard(
        definition = AutomationGeneratedDefinitionPreviewCatalog.trigger(TriggerType.TIME_BASED),
        config = TimeBasedTriggerConfig(
            hour = 8,
            minute = 30,
            days = setOf(Weekday.MONDAY, Weekday.WEDNESDAY, Weekday.FRIDAY),
        ),
    )
}

@Preview(showBackground = true, widthDp = 420)
@Composable
internal fun BatteryLevelTriggerDefinitionComposePreview() {
    AutomationDefinitionPreviewCard(
        definition = AutomationGeneratedDefinitionPreviewCatalog.trigger(TriggerType.BATTERY_LEVEL),
        config = BatteryLevelTriggerConfig(
            operator = ComparisonOperator.LESS_THAN_OR_EQUAL,
            value = 20,
        ),
    )
}

@Preview(showBackground = true, widthDp = 420)
@Composable
internal fun PowerConnectionTriggerDefinitionComposePreview() {
    AutomationDefinitionPreviewCard(
        definition = AutomationGeneratedDefinitionPreviewCatalog.trigger(TriggerType.POWER_CONNECTION),
        config = PowerConnectionTriggerConfig(state = PowerConnectionState.DISCONNECTED),
    )
}

@Preview(showBackground = true, widthDp = 420)
@Composable
internal fun BatterySaverStateTriggerDefinitionComposePreview() {
    AutomationDefinitionPreviewCard(
        definition = AutomationGeneratedDefinitionPreviewCatalog.trigger(TriggerType.BATTERY_SAVER_STATE),
        config = BatterySaverStateTriggerConfig(enabled = true),
    )
}

@Preview(showBackground = true, widthDp = 420)
@Composable
internal fun ApplicationLifecycleTriggerDefinitionComposePreview() {
    AutomationDefinitionPreviewCard(
        definition = AutomationGeneratedDefinitionPreviewCatalog.trigger(TriggerType.APPLICATION_LIFECYCLE),
        config = ApplicationLifecycleTriggerConfig(
            packageName = "com.spotify.music",
            transitionType = AppLifecycleTransitionType.CLOSED,
        ),
    )
}

@Preview(showBackground = true, widthDp = 420)
@Composable
internal fun NotificationReceivedTriggerDefinitionComposePreview() {
    AutomationDefinitionPreviewCard(
        definition = AutomationGeneratedDefinitionPreviewCatalog.trigger(TriggerType.NOTIFICATION_RECEIVED),
        config = NotificationReceivedTriggerConfig(packageName = "com.whatsapp"),
    )
}

@Preview(showBackground = true, widthDp = 420)
@Composable
internal fun WifiSsidTriggerDefinitionComposePreview() {
    AutomationDefinitionPreviewCard(
        definition = AutomationGeneratedDefinitionPreviewCatalog.trigger(TriggerType.WIFI_SSID_IN_RANGE),
        config = WifiSsidTriggerConfig(ssid = "Office WiFi"),
    )
}

@Preview(showBackground = true, widthDp = 420)
@Composable
internal fun GeofenceTriggerDefinitionComposePreview() {
    AutomationDefinitionPreviewCard(
        definition = AutomationGeneratedDefinitionPreviewCatalog.trigger(TriggerType.GEOFENCE),
        config = GeofenceTriggerConfig(
            geofenceId = "home",
            geofenceName = "Home",
            transitionType = GeofenceTransitionType.EXIT,
            updateRate = GeofenceUpdateRate.FAST,
        ),
    )
}

@Preview(showBackground = true, widthDp = 420)
@Composable
internal fun BatteryLevelConstraintDefinitionComposePreview() {
    AutomationDefinitionPreviewCard(
        definition = AutomationGeneratedDefinitionPreviewCatalog.constraint(ConstraintType.BATTERY_LEVEL),
        config = BatteryLevelConstraintConfig(
            operator = ComparisonOperator.LESS_THAN_OR_EQUAL,
            value = 20,
        ),
    )
}

@Preview(showBackground = true, widthDp = 420)
@Composable
internal fun BatterySaverStateConstraintDefinitionComposePreview() {
    AutomationDefinitionPreviewCard(
        definition = AutomationGeneratedDefinitionPreviewCatalog.constraint(ConstraintType.BATTERY_SAVER_STATE),
        config = BatterySaverStateConstraintConfig(enabled = true),
    )
}

@Preview(showBackground = true, widthDp = 420)
@Composable
internal fun PowerConnectedConstraintDefinitionComposePreview() {
    AutomationDefinitionPreviewCard(
        definition = AutomationGeneratedDefinitionPreviewCatalog.constraint(ConstraintType.POWER_CONNECTED),
        config = PowerConnectedConstraintConfig(connected = false),
    )
}

@Preview(showBackground = true, widthDp = 420)
@Composable
internal fun BluetoothStateConstraintDefinitionComposePreview() {
    AutomationDefinitionPreviewCard(
        definition = AutomationGeneratedDefinitionPreviewCatalog.constraint(ConstraintType.BLUETOOTH_STATE),
        config = BluetoothStateConstraintConfig(enabled = true),
    )
}

@Preview(showBackground = true, widthDp = 420)
@Composable
internal fun GpsStateConstraintDefinitionComposePreview() {
    AutomationDefinitionPreviewCard(
        definition = AutomationGeneratedDefinitionPreviewCatalog.constraint(ConstraintType.GPS_STATE),
        config = GpsStateConstraintConfig(enabled = true),
    )
}

@Preview(showBackground = true, widthDp = 420)
@Composable
internal fun MobileDataStateConstraintDefinitionComposePreview() {
    AutomationDefinitionPreviewCard(
        definition = AutomationGeneratedDefinitionPreviewCatalog.constraint(ConstraintType.MOBILE_DATA_STATE),
        config = MobileDataStateConstraintConfig(enabled = true),
    )
}

@Preview(showBackground = true, widthDp = 420)
@Composable
internal fun WifiStateConstraintDefinitionComposePreview() {
    AutomationDefinitionPreviewCard(
        definition = AutomationGeneratedDefinitionPreviewCatalog.constraint(ConstraintType.WIFI_STATE),
        config = WifiStateConstraintConfig(enabled = true),
    )
}

@Preview(showBackground = true, widthDp = 420)
@Composable
internal fun WifiSsidConstraintDefinitionComposePreview() {
    AutomationDefinitionPreviewCard(
        definition = AutomationGeneratedDefinitionPreviewCatalog.constraint(ConstraintType.WIFI_SSID_CONNECTED),
        config = WifiSsidConstraintConfig(ssid = "Office WiFi"),
    )
}

@Preview(showBackground = true, widthDp = 420)
@Composable
internal fun DateOfMonthConstraintDefinitionComposePreview() {
    AutomationDefinitionPreviewCard(
        definition = AutomationGeneratedDefinitionPreviewCatalog.constraint(ConstraintType.DATE_OF_MONTH),
        config = DateOfMonthConstraintConfig(days = AutomationGeneratedDefinitionPreviewCatalog.sampleMonthDays),
    )
}

@Preview(showBackground = true, widthDp = 420)
@Composable
internal fun DayOfWeekConstraintDefinitionComposePreview() {
    AutomationDefinitionPreviewCard(
        definition = AutomationGeneratedDefinitionPreviewCatalog.constraint(ConstraintType.DAY_OF_WEEK),
        config = DayOfWeekConstraintConfig(days = setOf(Weekday.MONDAY, Weekday.FRIDAY)),
    )
}

@Preview(showBackground = true, widthDp = 420)
@Composable
internal fun MonthOfYearConstraintDefinitionComposePreview() {
    AutomationDefinitionPreviewCard(
        definition = AutomationGeneratedDefinitionPreviewCatalog.constraint(ConstraintType.MONTH_OF_YEAR),
        config = MonthOfYearConstraintConfig(months = setOf(MonthOfYear.JUNE, MonthOfYear.DECEMBER)),
    )
}

@Preview(showBackground = true, widthDp = 420)
@Composable
internal fun TimeOfDayConstraintDefinitionComposePreview() {
    AutomationDefinitionPreviewCard(
        definition = AutomationGeneratedDefinitionPreviewCatalog.constraint(ConstraintType.TIME_OF_DAY),
        config = TimeOfDayConstraintConfig(
            startTime = AutomationLocalTime(hour = 22, minute = 0),
            endTime = AutomationLocalTime(hour = 6, minute = 30),
        ),
    )
}

@Preview(showBackground = true, widthDp = 420)
@Composable
internal fun GeofenceConstraintDefinitionComposePreview() {
    AutomationDefinitionPreviewCard(
        definition = AutomationGeneratedDefinitionPreviewCatalog.constraint(ConstraintType.GEOFENCE),
        config = GeofenceConstraintConfig(
            geofenceId = "home",
            geofenceName = "Home",
            latitude = 51.219448,
            longitude = 4.402464,
            radiusMeters = 150f,
            inside = false,
        ),
    )
}

@Preview(showBackground = true, widthDp = 420)
@Composable
internal fun HeadphoneConnectionConstraintDefinitionComposePreview() {
    AutomationDefinitionPreviewCard(
        definition = AutomationGeneratedDefinitionPreviewCatalog.constraint(ConstraintType.HEADPHONE_CONNECTION),
        config = HeadphoneConnectionConstraintConfig(connected = true),
    )
}

@Preview(showBackground = true, widthDp = 420)
@Composable
internal fun MusicActiveConstraintDefinitionComposePreview() {
    AutomationDefinitionPreviewCard(
        definition = AutomationGeneratedDefinitionPreviewCatalog.constraint(ConstraintType.MUSIC_ACTIVE),
        config = MusicActiveConstraintConfig(active = true),
    )
}

@Preview(showBackground = true, widthDp = 420)
@Composable
internal fun CallStateConstraintDefinitionComposePreview() {
    AutomationDefinitionPreviewCard(
        definition = AutomationGeneratedDefinitionPreviewCatalog.constraint(ConstraintType.CALL_STATE),
        config = CallStateConstraintConfig(inCall = false),
    )
}

@Preview(showBackground = true, widthDp = 420)
@Composable
internal fun ScreenStateConstraintDefinitionComposePreview() {
    AutomationDefinitionPreviewCard(
        definition = AutomationGeneratedDefinitionPreviewCatalog.constraint(ConstraintType.SCREEN_STATE),
        config = ScreenStateConstraintConfig(on = false),
    )
}

@Preview(showBackground = true, widthDp = 420)
@Composable
internal fun DoNotDisturbActionDefinitionComposePreview() {
    AutomationDefinitionPreviewCard(
        definition = AutomationGeneratedDefinitionPreviewCatalog.action(ActionType.DO_NOT_DISTURB),
        config = DoNotDisturbActionConfig(mode = DoNotDisturbMode.ALARMS_ONLY),
    )
}

@Preview(showBackground = true, widthDp = 420)
@Composable
internal fun ShowNotificationActionDefinitionComposePreview() {
    AutomationDefinitionPreviewCard(
        definition = AutomationGeneratedDefinitionPreviewCatalog.action(ActionType.SHOW_NOTIFICATION),
        config = ShowNotificationActionConfig(
            title = "Battery Saver",
            message = "Power saving mode has been enabled.",
        ),
    )
}

@Preview(showBackground = true, widthDp = 420)
@Composable
internal fun LogMessageActionDefinitionComposePreview() {
    AutomationDefinitionPreviewCard(
        definition = AutomationGeneratedDefinitionPreviewCatalog.action(ActionType.LOG_MESSAGE),
        config = LogMessageActionConfig(message = "Logged from automation preview coverage."),
    )
}

@Preview(showBackground = true, widthDp = 420)
@Composable
internal fun LaunchApplicationActionDefinitionComposePreview() {
    AutomationDefinitionPreviewCard(
        definition = AutomationGeneratedDefinitionPreviewCatalog.action(ActionType.LAUNCH_APPLICATION),
        config = LaunchApplicationActionConfig(
            packageName = "com.spotify.music",
            appLabel = "Spotify",
        ),
    )
}

@Preview(showBackground = true, widthDp = 420)
@Composable
internal fun OpenWebsiteActionDefinitionComposePreview() {
    AutomationDefinitionPreviewCard(
        definition = AutomationGeneratedDefinitionPreviewCatalog.action(ActionType.OPEN_WEBSITE),
        config = OpenWebsiteActionConfig(url = "https://orkestr.app"),
    )
}

@Preview(showBackground = true, widthDp = 420)
@Composable
internal fun VibratePhoneActionDefinitionComposePreview() {
    AutomationDefinitionPreviewCard(
        definition = AutomationGeneratedDefinitionPreviewCatalog.action(ActionType.VIBRATE_PHONE),
        config = VibratePhoneActionConfig(durationMillis = 700),
    )
}

@Preview(showBackground = true, widthDp = 420)
@Composable
internal fun FlashTorchActionDefinitionComposePreview() {
    AutomationDefinitionPreviewCard(
        definition = AutomationGeneratedDefinitionPreviewCatalog.action(ActionType.FLASH_TORCH),
        config = FlashTorchActionConfig(pulseCount = 4, onDurationMillis = 200, offDurationMillis = 150),
    )
}

@Preview(showBackground = true, widthDp = 420)
@Composable
internal fun SetWallpaperActionDefinitionComposePreview() {
    AutomationDefinitionPreviewCard(
        definition = AutomationGeneratedDefinitionPreviewCatalog.action(ActionType.SET_WALLPAPER),
        config = SetWallpaperActionConfig(
            imageUri = "content://media/external/images/media/42",
            imageLabel = "sunrise.jpg",
            target = WallpaperTarget.HOME_AND_LOCK_SCREEN,
        ),
    )
}

@Preview(showBackground = true, widthDp = 420)
@Composable
internal fun ForceLocationUpdateActionDefinitionComposePreview() {
    AutomationDefinitionPreviewCard(
        definition = AutomationGeneratedDefinitionPreviewCatalog.action(ActionType.FORCE_LOCATION_UPDATE),
        config = ForceLocationUpdateActionConfig(),
    )
}

@Preview(showBackground = true, widthDp = 420)
@Composable
internal fun SetPhoneVolumeActionDefinitionComposePreview() {
    AutomationDefinitionPreviewCard(
        definition = AutomationGeneratedDefinitionPreviewCatalog.action(ActionType.SET_PHONE_VOLUME),
        config = SetPhoneVolumeActionConfig(stream = PhoneVolumeStream.MEDIA, levelPercent = 35),
    )
}

@Preview(showBackground = true, widthDp = 420)
@Composable
internal fun SetPhoneVibrateActionDefinitionComposePreview() {
    AutomationDefinitionPreviewCard(
        definition = AutomationGeneratedDefinitionPreviewCatalog.action(ActionType.SET_PHONE_VIBRATE),
        config = SetPhoneVibrateActionConfig(enabled = true),
    )
}

@Composable
private fun AutomationDefinitionPreviewCard(definition: AutomationNodeDefinition<*, *>, config: AutomationConfig<*>?) {
    MaterialTheme {
        Surface(modifier = Modifier.padding(16.dp)) {
            OutlinedCard(modifier = Modifier.fillMaxWidth()) {
                AutomationCardColumn {
                    Row(
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = stringResource(definition.titleRes),
                                style = MaterialTheme.typography.titleLarge,
                            )
                            if (definition.isBeta) {
                                AutomationBetaChip()
                            }
                            definition.requiredMinSdk?.let { requiredMinSdk ->
                                AutomationRequiredSdkChip(requiredMinSdk = requiredMinSdk)
                            }
                        }
                    }
                    Text(
                        text = stringResource(definition.descriptionRes),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    if (definition.fields.isNotEmpty()) {
                        AutomationFieldForm(
                            fields = definition.fields,
                            config = config,
                            onFieldChanged = { _, _ -> },
                        )
                    }
                }
            }
        }
    }
}
