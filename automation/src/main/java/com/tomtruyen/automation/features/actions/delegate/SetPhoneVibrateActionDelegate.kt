package com.tomtruyen.automation.features.actions.delegate

import android.content.Context
import android.media.AudioManager
import com.tomtruyen.automation.codegen.GenerateActionDelegate
import com.tomtruyen.automation.core.event.AutomationEvent
import com.tomtruyen.automation.features.actions.ActionType
import com.tomtruyen.automation.features.actions.config.SetPhoneVibrateActionConfig

@GenerateActionDelegate
class SetPhoneVibrateActionDelegate(private val context: Context) : ActionDelegate<SetPhoneVibrateActionConfig> {
    override val type: ActionType = ActionType.SET_PHONE_VIBRATE

    override suspend fun execute(config: SetPhoneVibrateActionConfig, event: AutomationEvent) {
        val audioManager = context.getSystemService(AudioManager::class.java) ?: return
        @Suppress("DEPRECATION")
        runCatching {
            audioManager.ringerMode = if (config.enabled) {
                AudioManager.RINGER_MODE_VIBRATE
            } else {
                AudioManager.RINGER_MODE_NORMAL
            }
        }
    }
}
