package com.tomtruyen.automation.features.actions.delegate

import android.content.Context
import android.content.Intent
import com.tomtruyen.automation.codegen.GenerateActionDelegate
import com.tomtruyen.automation.core.event.AutomationEvent
import com.tomtruyen.automation.features.actions.ActionType
import com.tomtruyen.automation.features.actions.config.LaunchApplicationActionConfig

@GenerateActionDelegate
class LaunchApplicationActionDelegate(private val context: Context) : ActionDelegate<LaunchApplicationActionConfig> {
    override val type: ActionType = ActionType.LAUNCH_APPLICATION

    override suspend fun execute(config: LaunchApplicationActionConfig, event: AutomationEvent) {
        val launchIntent = context.packageManager.getLaunchIntentForPackage(config.packageName) ?: return
        launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        runCatching { context.startActivity(launchIntent) }
    }
}
