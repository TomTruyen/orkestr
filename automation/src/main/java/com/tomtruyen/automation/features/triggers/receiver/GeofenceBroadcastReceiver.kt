package com.tomtruyen.automation.features.triggers.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingEvent
import com.tomtruyen.automation.core.AutomationLogger
import com.tomtruyen.automation.core.AutomationRuntimeService
import com.tomtruyen.automation.core.event.GeofenceTransitionEvent
import com.tomtruyen.automation.core.model.GeofenceTransitionType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class GeofenceBroadcastReceiver :
    BroadcastReceiver(),
    KoinComponent {
    private val runtimeService by inject<AutomationRuntimeService>()
    private val logger by inject<AutomationLogger>()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override fun onReceive(context: Context, intent: Intent) {
        logger.debug("GeofenceBroadcastReceiver received intent action=${intent.action}")
        val pendingResult = runCatching { goAsync() }.getOrNull()
        scope.launch {
            try {
                handleIntent(intent)
            } finally {
                pendingResult?.finish()
            }
        }
    }

    private suspend fun handleIntent(intent: Intent) {
        val event = GeofencingEvent.fromIntent(intent)
        if (event == null) {
            logger.warning("Received geofence intent but could not parse GeofencingEvent")
            return
        }
        if (event.hasError()) {
            logger.error(
                "Geofence transition error: ${GeofenceStatusCodes.getStatusCodeString(
                    event.errorCode,
                )} (${event.errorCode})",
            )
            return
        }

        val transitionType = GeofenceTransitionType.fromGoogleTransition(event.geofenceTransition)
        if (transitionType == null) {
            logger.warning("Ignoring unsupported geofence transition: ${event.geofenceTransition}")
            return
        }

        logger.info("Received geofence transition ${transitionType.name}")

        event.triggeringGeofences.orEmpty().forEach { geofence ->
            logger.info("Received geofence transition ${transitionType.name} for ${geofence.requestId}")
            runtimeService.handleEvent(
                GeofenceTransitionEvent(
                    geofenceId = geofence.requestId,
                    transitionType = transitionType,
                ),
            )
        }
    }
}
