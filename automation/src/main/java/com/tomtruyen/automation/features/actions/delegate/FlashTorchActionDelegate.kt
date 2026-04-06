package com.tomtruyen.automation.features.actions.delegate

import android.content.Context
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import com.tomtruyen.automation.codegen.GenerateActionDelegate
import com.tomtruyen.automation.core.event.AutomationEvent
import com.tomtruyen.automation.features.actions.ActionType
import com.tomtruyen.automation.features.actions.config.FlashTorchActionConfig
import kotlinx.coroutines.delay

@GenerateActionDelegate
class FlashTorchActionDelegate(private val context: Context) : ActionDelegate<FlashTorchActionConfig> {
    override val type: ActionType = ActionType.FLASH_TORCH

    override suspend fun execute(config: FlashTorchActionConfig, event: AutomationEvent) {
        if (!config.requiredPermissions.all { it.isGranted(context) }) return

        val cameraManager = context.getSystemService(CameraManager::class.java) ?: return
        val cameraId = cameraManager.cameraIdList.firstOrNull { id ->
            val characteristics = cameraManager.getCameraCharacteristics(id)
            characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
        } ?: return

        val pulseCount = config.pulseCount.coerceIn(MIN_PULSE_COUNT, MAX_PULSE_COUNT)
        val onDuration = config.onDurationMillis.toLong().coerceIn(MIN_DURATION_MILLIS, MAX_DURATION_MILLIS)
        val offDuration = config.offDurationMillis.toLong().coerceIn(MIN_DURATION_MILLIS, MAX_DURATION_MILLIS)

        runCatching {
            repeat(pulseCount) { index ->
                cameraManager.setTorchMode(cameraId, true)
                delay(onDuration)
                cameraManager.setTorchMode(cameraId, false)
                if (index < pulseCount - 1) {
                    delay(offDuration)
                }
            }
        }.also {
            runCatching { cameraManager.setTorchMode(cameraId, false) }
        }
    }

    private companion object {
        const val MIN_PULSE_COUNT = 1
        const val MAX_PULSE_COUNT = 10
        const val MIN_DURATION_MILLIS = 50L
        const val MAX_DURATION_MILLIS = 5_000L
    }
}
