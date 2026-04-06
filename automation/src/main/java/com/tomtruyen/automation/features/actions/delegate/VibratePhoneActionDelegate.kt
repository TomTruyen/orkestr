package com.tomtruyen.automation.features.actions.delegate

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import com.tomtruyen.automation.codegen.GenerateActionDelegate
import com.tomtruyen.automation.core.event.AutomationEvent
import com.tomtruyen.automation.features.actions.ActionType
import com.tomtruyen.automation.features.actions.config.VibratePhoneActionConfig

@GenerateActionDelegate
class VibratePhoneActionDelegate(private val context: Context) : ActionDelegate<VibratePhoneActionConfig> {
    override val type: ActionType = ActionType.VIBRATE_PHONE

    override suspend fun execute(config: VibratePhoneActionConfig, event: AutomationEvent) {
        val durationMillis = config.durationMillis.toLong().coerceIn(MIN_DURATION_MILLIS, MAX_DURATION_MILLIS)
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            context.getSystemService(VibratorManager::class.java)?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        } ?: return

        val effect = VibrationEffect.createOneShot(durationMillis, VibrationEffect.DEFAULT_AMPLITUDE)
        vibrator.vibrate(effect)
    }

    private companion object {
        const val MIN_DURATION_MILLIS = 100L
        const val MAX_DURATION_MILLIS = 10_000L
    }
}
