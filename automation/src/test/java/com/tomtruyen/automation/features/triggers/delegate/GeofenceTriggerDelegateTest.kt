package com.tomtruyen.automation.features.triggers.delegate

import com.tomtruyen.automation.core.event.AutomationEvent
import com.tomtruyen.automation.core.event.GeofenceTransitionEvent
import com.tomtruyen.automation.core.model.GeofenceTransitionType
import com.tomtruyen.automation.features.triggers.config.GeofenceTriggerConfig
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

internal class GeofenceTriggerDelegateTest {
    private val delegate = GeofenceTriggerDelegate()

    @Test
    fun matches_whenGeofenceIdAndTransitionMatch_returnsTrue() {
        val config = GeofenceTriggerConfig(
            geofenceId = "home",
            geofenceName = "Home",
            transitionType = GeofenceTransitionType.ENTER,
        )
        val event = GeofenceTransitionEvent(
            geofenceId = "home",
            transitionType = GeofenceTransitionType.ENTER,
        )

        assertTrue(delegate.matches(config, event))
    }

    @Test
    fun matches_whenEventDoesNotMatch_returnsFalse() {
        val config = GeofenceTriggerConfig(
            geofenceId = "home",
            geofenceName = "Home",
            transitionType = GeofenceTransitionType.EXIT,
        )
        val wrongGeofence = GeofenceTransitionEvent(
            geofenceId = "work",
            transitionType = GeofenceTransitionType.EXIT,
        )
        val wrongTransition = GeofenceTransitionEvent(
            geofenceId = "home",
            transitionType = GeofenceTransitionType.ENTER,
        )
        val differentEvent = object : AutomationEvent() {}

        assertFalse(delegate.matches(config, wrongGeofence))
        assertFalse(delegate.matches(config, wrongTransition))
        assertFalse(delegate.matches(config, differentEvent))
    }
}
