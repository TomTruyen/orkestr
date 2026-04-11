package com.tomtruyen.automation.features.triggers.delegate

import com.tomtruyen.automation.codegen.GenerateTriggerDelegate
import com.tomtruyen.automation.core.event.AutomationEvent
import com.tomtruyen.automation.core.event.NetworkConnectivityEvent
import com.tomtruyen.automation.features.triggers.TriggerType
import com.tomtruyen.automation.features.triggers.config.NetworkConnectivityTriggerConfig

@GenerateTriggerDelegate
class NetworkConnectivityTriggerDelegate : TriggerDelegate<NetworkConnectivityTriggerConfig> {
    override val type: TriggerType = TriggerType.NETWORK_CONNECTIVITY

    override fun matches(config: NetworkConnectivityTriggerConfig, event: AutomationEvent): Boolean =
        event is NetworkConnectivityEvent && event.connected == config.connected
}
