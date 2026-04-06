package com.tomtruyen.automation.features.actions.delegate

import android.content.Context
import com.tomtruyen.automation.codegen.GenerateActionDelegate
import com.tomtruyen.automation.core.event.AutomationEvent
import com.tomtruyen.automation.core.location.AutomationLocationRequester
import com.tomtruyen.automation.features.actions.ActionType
import com.tomtruyen.automation.features.actions.config.ForceLocationUpdateActionConfig

@GenerateActionDelegate
class ForceLocationUpdateActionDelegate(context: Context) : ActionDelegate<ForceLocationUpdateActionConfig> {
    override val type: ActionType = ActionType.FORCE_LOCATION_UPDATE
    private val locationRequester = AutomationLocationRequester(context)
    private val appContext = context

    override suspend fun execute(config: ForceLocationUpdateActionConfig, event: AutomationEvent) {
        if (!config.requiredPermissions.all { it.isGranted(appContext) }) return
        locationRequester.requestCurrentLocation()
    }
}
