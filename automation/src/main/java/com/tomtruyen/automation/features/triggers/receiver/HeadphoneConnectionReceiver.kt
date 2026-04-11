package com.tomtruyen.automation.features.triggers.receiver

import android.content.Context
import android.content.Intent
import android.media.AudioDeviceCallback
import android.media.AudioDeviceInfo
import android.media.AudioManager
import com.tomtruyen.automation.codegen.GenerateReceiverFactory
import com.tomtruyen.automation.core.AutomationLogger
import com.tomtruyen.automation.core.AutomationRuntimeService
import com.tomtruyen.automation.core.event.HeadphoneConnectionEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class HeadphoneConnectionReceiver(
    override val service: AutomationRuntimeService,
    override val scope: CoroutineScope,
    override val logger: AutomationLogger,
    private val audioManager: AudioManager?,
    private val isConnectedProvider: () -> Boolean,
) : TriggerReceiver() {
    private var lastConnected: Boolean = isConnectedProvider()
    private val callback = object : AudioDeviceCallback() {
        override fun onAudioDevicesAdded(addedDevices: Array<out AudioDeviceInfo>) = emitIfChanged()
        override fun onAudioDevicesRemoved(removedDevices: Array<out AudioDeviceInfo>) = emitIfChanged()
    }

    override fun onReceive(context: Context, intent: Intent) = Unit

    internal fun start() {
        audioManager?.registerAudioDeviceCallback(callback, null)
    }

    override fun onUnregister(context: Context) {
        audioManager?.unregisterAudioDeviceCallback(callback)
    }

    internal fun emitIfChanged() {
        val connected = isConnectedProvider()
        if (connected == lastConnected) return
        lastConnected = connected
        logger.info("Received headphone connection event connected=$connected")
        scope.launch {
            service.handleEvent(HeadphoneConnectionEvent(connected))
        }
    }

    @GenerateReceiverFactory
    companion object Factory : TriggerFactory {
        override val key: TriggerReceiverKey = TriggerReceiverKey.HEADPHONE_CONNECTION

        override fun register(
            context: Context,
            service: AutomationRuntimeService,
            scope: CoroutineScope,
            logger: AutomationLogger,
        ): TriggerReceiver {
            val audioManager = context.getSystemService(AudioManager::class.java)
            val receiver = HeadphoneConnectionReceiver(
                service = service,
                scope = scope,
                logger = logger,
                audioManager = audioManager,
                isConnectedProvider = {
                    audioManager?.getDevices(AudioManager.GET_DEVICES_OUTPUTS).orEmpty().any { device ->
                        device.type in HEADPHONE_DEVICE_TYPES
                    }
                },
            )
            receiver.start()
            return receiver
        }

        private val HEADPHONE_DEVICE_TYPES = setOf(
            AudioDeviceInfo.TYPE_WIRED_HEADSET,
            AudioDeviceInfo.TYPE_WIRED_HEADPHONES,
            AudioDeviceInfo.TYPE_BLUETOOTH_A2DP,
            AudioDeviceInfo.TYPE_BLUETOOTH_SCO,
            AudioDeviceInfo.TYPE_USB_HEADSET,
            AudioDeviceInfo.TYPE_BLE_HEADSET,
        )
    }
}
