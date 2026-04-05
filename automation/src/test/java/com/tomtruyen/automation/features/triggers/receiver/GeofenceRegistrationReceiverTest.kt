package com.tomtruyen.automation.features.triggers.receiver

import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingRequest
import com.tomtruyen.automation.core.AutomationRule
import com.tomtruyen.automation.core.model.AutomationGeofence
import com.tomtruyen.automation.core.model.GeofenceTransitionType
import com.tomtruyen.automation.core.model.GeofenceUpdateRate
import com.tomtruyen.automation.features.triggers.config.GeofenceTriggerConfig
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

internal class GeofenceRegistrationReceiverTest {
    @Test
    fun geofencesToRegister_usesConfiguredTransitionTypeForSingleGeofence() {
        val geofence = sampleGeofence(id = "home")
        val rules = listOf(
            sampleRule(
                GeofenceTriggerConfig(
                    geofenceId = geofence.id,
                    geofenceName = geofence.name,
                    transitionType = GeofenceTransitionType.EXIT,
                    updateRate = GeofenceUpdateRate.RELAXED,
                ),
            ),
        )

        val registrations = geofencesToRegister(
            rules = rules,
            geofences = listOf(geofence),
        )

        assertEquals(1, registrations.size)
        assertEquals(Geofence.GEOFENCE_TRANSITION_EXIT, registrations.single().transitionMask)
        assertEquals(
            GeofenceUpdateRate.RELAXED.notificationResponsivenessMillis,
            registrations.single().notificationResponsivenessMillis,
        )
    }

    @Test
    fun geofencesToRegister_mergesTransitionsForSameGeofenceAcrossRules() {
        val geofence = sampleGeofence(id = "home")
        val rules = listOf(
            sampleRule(
                GeofenceTriggerConfig(
                    geofenceId = geofence.id,
                    geofenceName = geofence.name,
                    transitionType = GeofenceTransitionType.ENTER,
                    updateRate = GeofenceUpdateRate.RELAXED,
                ),
            ),
            sampleRule(
                GeofenceTriggerConfig(
                    geofenceId = geofence.id,
                    geofenceName = geofence.name,
                    transitionType = GeofenceTransitionType.EXIT,
                    updateRate = GeofenceUpdateRate.FAST,
                ),
            ),
        )

        val registrations = geofencesToRegister(
            rules = rules,
            geofences = listOf(geofence),
        )

        assertEquals(1, registrations.size)
        assertEquals(
            Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT,
            registrations.single().transitionMask,
        )
        assertEquals(
            GeofenceUpdateRate.FAST.notificationResponsivenessMillis,
            registrations.single().notificationResponsivenessMillis,
        )
    }

    @Test
    fun initialTriggerMask_includesOnlyConfiguredTransitionDirections() {
        val registrations = listOf(
            RegisteredGeofence(
                geofence = sampleGeofence(id = "home"),
                transitionMask = Geofence.GEOFENCE_TRANSITION_EXIT,
                notificationResponsivenessMillis = GeofenceUpdateRate.BALANCED.notificationResponsivenessMillis,
            ),
        )

        val initialTriggerMask = initialTriggerMask(registrations)

        assertEquals(GeofencingRequest.INITIAL_TRIGGER_EXIT, initialTriggerMask)
    }

    @Test
    fun initialTriggerMask_includesBothDirectionsWhenNeeded() {
        val registrations = listOf(
            RegisteredGeofence(
                geofence = sampleGeofence(id = "home"),
                transitionMask = Geofence.GEOFENCE_TRANSITION_ENTER,
                notificationResponsivenessMillis = GeofenceUpdateRate.BALANCED.notificationResponsivenessMillis,
            ),
            RegisteredGeofence(
                geofence = sampleGeofence(id = "work"),
                transitionMask = Geofence.GEOFENCE_TRANSITION_EXIT,
                notificationResponsivenessMillis = GeofenceUpdateRate.FAST.notificationResponsivenessMillis,
            ),
        )

        val initialTriggerMask = initialTriggerMask(registrations)

        assertTrue(initialTriggerMask and GeofencingRequest.INITIAL_TRIGGER_ENTER != 0)
        assertTrue(initialTriggerMask and GeofencingRequest.INITIAL_TRIGGER_EXIT != 0)
    }

    private fun sampleRule(trigger: GeofenceTriggerConfig): AutomationRule = AutomationRule(
        id = "${trigger.geofenceId}-${trigger.transitionType.name.lowercase()}",
        name = "Rule ${trigger.geofenceId}",
        enabled = true,
        triggers = listOf(trigger),
        constraints = emptyList(),
        actions = emptyList(),
    )

    private fun sampleGeofence(id: String): AutomationGeofence = AutomationGeofence(
        id = id,
        name = id.replaceFirstChar { it.uppercase() },
        latitude = 51.0,
        longitude = 4.0,
        radiusMeters = 150f,
        address = null,
    )
}
