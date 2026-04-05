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
) : TriggerReceiver(), KoinComponent {
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
                val enabledGeofenceConfigs = rules
                    .asSequence()
                    .filter { it.enabled }
                    .flatMap { rule -> rule.triggers.asSequence() }
                    .filterIsInstance<GeofenceTriggerConfig>()
                    .toList()

                val geofenceMap = geofences.associateBy { it.id }
                enabledGeofenceConfigs
                    .mapNotNull { config -> geofenceMap[config.geofenceId] }
                    .distinctBy { it.id }
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
    private fun syncGeofences(geofences: List<AutomationGeofence>) {
        val targetIds = geofences.map { it.id }.toSet()
        val staleIds = activeGeofenceIds - targetIds

        if (staleIds.isNotEmpty()) {
            geofencingClient.removeGeofences(staleIds.toList())
        }

        if (targetIds.isEmpty()) {
            activeGeofenceIds = emptySet()
            return
        }

        val request = GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .addGeofences(geofences.map { it.toGoogleGeofence() })
            .build()

        geofencingClient.removeGeofences(geofencePendingIntent(appContext)).addOnCompleteListener {
            geofencingClient.addGeofences(request, geofencePendingIntent(appContext))
                .addOnSuccessListener {
                    activeGeofenceIds = targetIds
                    logger.log("Registered ${targetIds.size} geofence(s) for action $ACTION_GEOFENCE_TRANSITION")
                }
                .addOnFailureListener { error ->
                    logger.log("Failed to register geofences: ${error.message}")
                }
        }
    }

    private fun AutomationGeofence.toGoogleGeofence(): Geofence = Geofence.Builder()
        .setRequestId(id)
        .setCircularRegion(latitude, longitude, radiusMeters)
        .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT)
        .setExpirationDuration(Geofence.NEVER_EXPIRE)
        .build()

    @GenerateReceiverFactory
    companion object Factory : TriggerFactory {
        override val key: TriggerReceiverKey = TriggerReceiverKey.GEOFENCE
        private const val ACTION_GEOFENCE_TRANSITION = "com.tomtruyen.automation.action.GEOFENCE_TRANSITION"

        private fun geofencePendingIntent(context: Context): PendingIntent {
            val intent = Intent(context, GeofenceBroadcastReceiver::class.java)
                .setAction(ACTION_GEOFENCE_TRANSITION)
                .setPackage(context.packageName)
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
