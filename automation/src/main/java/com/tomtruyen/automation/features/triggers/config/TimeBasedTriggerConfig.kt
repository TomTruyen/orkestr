package com.tomtruyen.automation.features.triggers.config

import com.tomtruyen.automation.core.config.AutomationCategory
import com.tomtruyen.automation.core.model.Weekday
import com.tomtruyen.automation.features.triggers.TriggerType
import com.tomtruyen.automation.features.triggers.receiver.TriggerReceiverKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName(TimeBasedTriggerConfig.DISCRIMINATOR)
data class TimeBasedTriggerConfig(
    val hour: Int = 9,
    val minute: Int = 0,
    val days: Set<Weekday> = Weekday.entries.toSet(),
) : TriggerConfig {
    override val type: TriggerType = TriggerType.TIME_BASED
    override val category: AutomationCategory = AutomationCategory.UTILITY
    override val requiredReceiverKeys: Set<TriggerReceiverKey> = setOf(TriggerReceiverKey.TIME_TICK)

    companion object {
        const val DISCRIMINATOR = "time_based"
    }
}
