package com.tomtruyen.automation.features.actions.delegate

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import com.tomtruyen.automation.codegen.GenerateActionDelegate
import com.tomtruyen.automation.core.event.AutomationEvent
import com.tomtruyen.automation.features.actions.ActionType
import com.tomtruyen.automation.features.actions.config.OpenWebsiteActionConfig

@GenerateActionDelegate
class OpenWebsiteActionDelegate(private val context: Context) : ActionDelegate<OpenWebsiteActionConfig> {
    override val type: ActionType = ActionType.OPEN_WEBSITE

    override suspend fun execute(config: OpenWebsiteActionConfig, event: AutomationEvent) {
        val trimmedUrl = config.url.trim().lowercase()
        if (trimmedUrl.isBlank()) return

        val normalizedUrl = if (trimmedUrl.contains("://")) trimmedUrl else "https://$trimmedUrl"
        val intent = Intent(Intent.ACTION_VIEW, normalizedUrl.toUri()).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        runCatching { context.startActivity(intent) }
    }
}
