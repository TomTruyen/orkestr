package com.tomtruyen.automation.features.triggers.config

import com.tomtruyen.automation.core.config.AutomationCategory
import com.tomtruyen.automation.features.triggers.TriggerType
import com.tomtruyen.automation.features.triggers.receiver.TriggerReceiverKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName(NetworkConnectivityTriggerConfig.DISCRIMINATOR)
data class NetworkConnectivityTriggerConfig(val connected: Boolean = true) : TriggerConfig {
    override val type: TriggerType = TriggerType.NETWORK_CONNECTIVITY
    override val category: AutomationCategory = AutomationCategory.UTILITY
    override val requiredReceiverKeys: Set<TriggerReceiverKey> = setOf(TriggerReceiverKey.NETWORK_CONNECTIVITY)

    companion object {
        const val DISCRIMINATOR = "network_connectivity"
    }
}
