package com.tomtruyen.automation.features.triggers.receiver

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.tomtruyen.automation.codegen.GenerateReceiverFactory
import com.tomtruyen.automation.core.AutomationLogger
import com.tomtruyen.automation.core.AutomationRuntimeService
import com.tomtruyen.automation.core.model.AutomationGeofence
import com.tomtruyen.automation.data.repository.AutomationRuleRepository
import com.tomtruyen.automation.data.repository.GeofenceRepository
import com.tomtruyen.automation.features.triggers.config.GeofenceTriggerConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class GeofenceRegistrationReceiver(
    context: Context,
    override val service: AutomationRuntimeService,
    override val scope: CoroutineScope,
    override val logger: AutomationLogger,
) : TriggerReceiver(),
    KoinComponent {
    private val appContext = context.applicationContext
    private val geofencingClient: GeofencingClient = LocationServices.getGeofencingClient(appContext)
    private val geofenceRepository by inject<GeofenceRepository>()
    private val ruleRepository by inject<AutomationRuleRepository>()
    private var activeGeofenceIds: Set<String> = emptySet()

    init {
        scope.launch {
            combine(
                ruleRepository.observeRules(),
                geofenceRepository.observeGeofences(),
            ) { rules, geofences ->
                geofencesToRegister(rules, geofences)
            }.distinctUntilChanged().collect(::syncGeofences)
        }
    }

    override fun onReceive(context: Context, intent: Intent) = Unit

    override fun onUnregister(context: Context) {
        if (activeGeofenceIds.isEmpty()) return
        geofencingClient.removeGeofences(activeGeofenceIds.toList())
        activeGeofenceIds = emptySet()
    }

    @SuppressLint("MissingPermission")
    private fun syncGeofences(geofences: List<RegisteredGeofence>) {
        val targetIds = geofences.map { it.geofence.id }.toSet()
        val staleIds = activeGeofenceIds - targetIds

        if (staleIds.isNotEmpty()) {
            geofencingClient.removeGeofences(staleIds.toList())
        }

        if (targetIds.isEmpty()) {
            activeGeofenceIds = emptySet()
            return
        }

        val request = GeofencingRequest.Builder()
            .setInitialTrigger(initialTriggerMask(geofences))
            .addGeofences(geofences.map { it.toGoogleGeofence() })
            .build()

        geofencingClient.removeGeofences(geofencePendingIntent(appContext)).addOnCompleteListener {
            geofencingClient.addGeofences(request, geofencePendingIntent(appContext))
                .addOnSuccessListener {
                    activeGeofenceIds = targetIds
                    logger.log("Registered ${targetIds.size} geofence(s) for action")
                }
                .addOnFailureListener { error ->
                    logger.log("Failed to register geofences: ${error.message}")
                }
        }
    }

    private fun RegisteredGeofence.toGoogleGeofence(): Geofence = Geofence.Builder()
        .setRequestId(geofence.id)
        .setCircularRegion(geofence.latitude, geofence.longitude, geofence.radiusMeters)
        .setTransitionTypes(transitionMask)
        .setNotificationResponsiveness(notificationResponsivenessMillis)
        .setExpirationDuration(Geofence.NEVER_EXPIRE)
        .build()

    @GenerateReceiverFactory
    companion object Factory : TriggerFactory {
        override val key: TriggerReceiverKey = TriggerReceiverKey.GEOFENCE
        private fun geofencePendingIntent(context: Context): PendingIntent {
            val intent = Intent(context, GeofenceBroadcastReceiver::class.java).setPackage(context.packageName)
            val flags = PendingIntent.FLAG_UPDATE_CURRENT or if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                PendingIntent.FLAG_MUTABLE
            } else {
                0
            }
            return PendingIntent.getBroadcast(
                context,
                0,
                intent,
                flags,
            )
        }

        override fun register(
            context: Context,
            service: AutomationRuntimeService,
            scope: CoroutineScope,
            logger: AutomationLogger,
        ): TriggerReceiver = GeofenceRegistrationReceiver(context, service, scope, logger)
    }
}

internal data class RegisteredGeofence(
    val geofence: AutomationGeofence,
    val transitionMask: Int,
    val notificationResponsivenessMillis: Int,
)

internal fun geofencesToRegister(
    rules: List<com.tomtruyen.automation.core.AutomationRule>,
    geofences: List<AutomationGeofence>,
): List<RegisteredGeofence> {
    val geofenceMap = geofences.associateBy { it.id }
    return rules
        .asSequence()
        .filter { it.enabled }
        .flatMap { rule -> rule.triggers.asSequence() }
        .filterIsInstance<GeofenceTriggerConfig>()
        .mapNotNull { config ->
            geofenceMap[config.geofenceId]?.let { geofence ->
                RegisteredGeofence(
                    geofence = geofence,
                    transitionMask = config.transitionType.toGoogleTransition(),
                    notificationResponsivenessMillis = config.updateRate.notificationResponsivenessMillis,
                )
            }
        }
        .groupBy { it.geofence.id }
        .values
        .map { registrations ->
            registrations.reduce { acc, registration ->
                acc.copy(
                    transitionMask = acc.transitionMask or registration.transitionMask,
                    notificationResponsivenessMillis = minOf(
                        acc.notificationResponsivenessMillis,
                        registration.notificationResponsivenessMillis,
                    ),
                )
            }
        }
        .sortedBy { it.geofence.id }
}

internal fun initialTriggerMask(geofences: List<RegisteredGeofence>): Int {
    val includesEnter = geofences.any { it.transitionMask and Geofence.GEOFENCE_TRANSITION_ENTER != 0 }
    val includesExit = geofences.any { it.transitionMask and Geofence.GEOFENCE_TRANSITION_EXIT != 0 }
    var mask = 0
    if (includesEnter) mask = mask or GeofencingRequest.INITIAL_TRIGGER_ENTER
    if (includesExit) mask = mask or GeofencingRequest.INITIAL_TRIGGER_EXIT
    return mask
}
