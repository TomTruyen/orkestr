package com.tomtruyen.automation.features.actions.delegate

import android.content.Context
import android.media.AudioManager
import com.tomtruyen.automation.codegen.GenerateActionDelegate
import com.tomtruyen.automation.core.event.AutomationEvent
import com.tomtruyen.automation.core.model.PhoneVolumeStream
import com.tomtruyen.automation.features.actions.ActionType
import com.tomtruyen.automation.features.actions.config.SetPhoneVolumeActionConfig

@GenerateActionDelegate
class SetPhoneVolumeActionDelegate(private val context: Context) : ActionDelegate<SetPhoneVolumeActionConfig> {
    override val type: ActionType = ActionType.SET_PHONE_VOLUME

    override suspend fun execute(config: SetPhoneVolumeActionConfig, event: AutomationEvent) {
        val audioManager = context.getSystemService(AudioManager::class.java) ?: return
        val streamType = config.stream.toAudioStream()
        val maxVolume = audioManager.getStreamMaxVolume(streamType)
        if (maxVolume <= 0) return

        val volume = (
            (config.levelPercent.coerceIn(MIN_LEVEL_PERCENT, MAX_LEVEL_PERCENT) / PERCENT_DIVISOR) * maxVolume
            ).toInt().coerceIn(MIN_VOLUME, maxVolume)
        audioManager.setStreamVolume(streamType, volume, VOLUME_FLAGS)
    }

    private fun PhoneVolumeStream.toAudioStream(): Int = when (this) {
        PhoneVolumeStream.MEDIA -> AudioManager.STREAM_MUSIC
        PhoneVolumeStream.RING -> AudioManager.STREAM_RING
        PhoneVolumeStream.CALL -> AudioManager.STREAM_VOICE_CALL
    }

    private companion object {
        const val MIN_LEVEL_PERCENT = 0
        const val MAX_LEVEL_PERCENT = 100
        const val PERCENT_DIVISOR = 100f
        const val MIN_VOLUME = 0
        const val VOLUME_FLAGS = 0
    }
}
