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

        val volume = ((config.levelPercent.coerceIn(0, 100) / 100f) * maxVolume).toInt()
            .coerceIn(0, maxVolume)
        audioManager.setStreamVolume(streamType, volume, 0)
    }

    private fun PhoneVolumeStream.toAudioStream(): Int = when (this) {
        PhoneVolumeStream.MEDIA -> AudioManager.STREAM_MUSIC
        PhoneVolumeStream.RING -> AudioManager.STREAM_RING
        PhoneVolumeStream.CALL -> AudioManager.STREAM_VOICE_CALL
    }
}
