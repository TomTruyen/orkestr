package com.tomtruyen.automation.features.triggers.config

import com.tomtruyen.automation.core.config.AutomationConfig
import com.tomtruyen.automation.features.triggers.TriggerType
import com.tomtruyen.automation.features.triggers.receiver.TriggerReceiverKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface TriggerConfig : AutomationConfig<TriggerType> {
    val requiredReceiverKeys: Set<TriggerReceiverKey>
        get() = emptySet()
}
