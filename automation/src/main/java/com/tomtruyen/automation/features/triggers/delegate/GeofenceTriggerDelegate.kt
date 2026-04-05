package com.tomtruyen.automation.features.triggers.delegate

import com.tomtruyen.automation.codegen.GenerateTriggerDelegate
import com.tomtruyen.automation.core.event.AutomationEvent
import com.tomtruyen.automation.core.event.GeofenceTransitionEvent
import com.tomtruyen.automation.features.triggers.TriggerType
import com.tomtruyen.automation.features.triggers.config.GeofenceTriggerConfig

@GenerateTriggerDelegate
class GeofenceTriggerDelegate : TriggerDelegate<GeofenceTriggerConfig> {
    override val type: TriggerType = TriggerType.GEOFENCE

    override fun matches(config: GeofenceTriggerConfig, event: AutomationEvent): Boolean =
        event is GeofenceTransitionEvent &&
            event.geofenceId == config.geofenceId &&
            event.transitionType == config.transitionType
}
