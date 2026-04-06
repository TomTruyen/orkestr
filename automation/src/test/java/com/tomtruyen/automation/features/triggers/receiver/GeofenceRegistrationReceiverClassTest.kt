package com.tomtruyen.automation.features.triggers.receiver

import android.app.PendingIntent
import android.content.pm.PackageManager
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.core.content.ContextCompat
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.tomtruyen.automation.core.AutomationLogger
import com.tomtruyen.automation.core.AutomationRule
import com.tomtruyen.automation.core.AutomationRuntimeService
import com.tomtruyen.automation.core.model.AutomationGeofence
import com.tomtruyen.automation.core.model.GeofenceUpdateRate
import com.tomtruyen.automation.data.repository.AutomationRuleRepository
import com.tomtruyen.automation.data.repository.GeofenceRepository
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.runs
import io.mockk.slot
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.robolectric.RobolectricTestRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
internal class GeofenceRegistrationReceiverClassTest {
    @MockK
    private lateinit var geofencingClient: GeofencingClient

    @MockK
    private lateinit var geofenceRepository: GeofenceRepository

    @MockK
    private lateinit var ruleRepository: AutomationRuleRepository

    @MockK
    private lateinit var runtimeService: AutomationRuntimeService

    @MockK
    private lateinit var logger: AutomationLogger

    private lateinit var context: Context
    private lateinit var rulesFlow: MutableStateFlow<List<AutomationRule>>
    private lateinit var geofencesFlow: MutableStateFlow<List<AutomationGeofence>>
    private lateinit var scope: TestScope

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        context = ApplicationProvider.getApplicationContext()
        rulesFlow = MutableStateFlow(emptyList())
        geofencesFlow = MutableStateFlow(emptyList())
        scope = TestScope()

        mockkStatic(LocationServices::class)
        mockkStatic(ContextCompat::class)
        every { LocationServices.getGeofencingClient(any()) } returns geofencingClient
        every { ContextCompat.checkSelfPermission(any(), any()) } returns PackageManager.PERMISSION_GRANTED
        every { geofenceRepository.observeGeofences() } returns geofencesFlow
        every { ruleRepository.observeRules() } returns rulesFlow
        every { logger.log(any()) } just runs
        every { geofencingClient.removeGeofences(any<List<String>>()) } returns immediateTask()
        every { geofencingClient.removeGeofences(any<PendingIntent>()) } returns immediateCompleteTask()
        every { geofencingClient.addGeofences(any(), any<PendingIntent>()) } returns immediateSuccessTask()

        stopKoin()
        startKoin {
            modules(
                module {
                    single<GeofenceRepository> { this@GeofenceRegistrationReceiverClassTest.geofenceRepository }
                    single<AutomationRuleRepository> { this@GeofenceRegistrationReceiverClassTest.ruleRepository }
                },
            )
        }
    }

    @After
    fun tearDown() {
        stopKoin()
        unmockkAll()
    }

    @Test
    fun syncGeofences_whenMatchingGeofenceExists_registersRequestWithConfiguredTransitionsAndResponsiveness() =
        runTest {
            val requestSlot = slot<GeofencingRequest>()
            every {
                geofencingClient.addGeofences(
                    capture(requestSlot),
                    any<PendingIntent>(),
                )
            } returns immediateSuccessTask()
            val receiver = receiver()

            invokeSyncGeofences(
                receiver = receiver,
                geofences = listOf(
                    RegisteredGeofence(
                        geofence = sampleGeofence("home"),
                        transitionMask = Geofence.GEOFENCE_TRANSITION_EXIT,
                        notificationResponsivenessMillis = GeofenceUpdateRate.RELAXED.notificationResponsivenessMillis,
                    ),
                ),
            )

            verify { geofencingClient.removeGeofences(any<PendingIntent>()) }
            verify { geofencingClient.addGeofences(any(), any<PendingIntent>()) }
            val request = requestSlot.captured
            val registeredGeofence = request.geofences.single()
            assertEquals("home", registeredGeofence.requestId)
            assertEquals(Geofence.GEOFENCE_TRANSITION_EXIT, registeredGeofence.transitionTypes)
            assertEquals(
                GeofenceUpdateRate.RELAXED.notificationResponsivenessMillis,
                registeredGeofence.notificationResponsiveness,
            )
            assertEquals(GeofencingRequest.INITIAL_TRIGGER_EXIT, request.initialTrigger)
        }

    @Test
    fun onUnregister_whenGeofencesWereActive_removesRegisteredIds() = runTest {
        val receiver = receiver()

        setActiveGeofenceIds(receiver, setOf("home"))
        receiver.onUnregister(context)

        verify { geofencingClient.removeGeofences(listOf("home")) }
    }

    @Test
    fun syncGeofences_whenTargetSetBecomesEmpty_unregistersStaleGeofenceIds() = runTest {
        val receiver = receiver()

        setActiveGeofenceIds(receiver, setOf("home"))

        invokeSyncGeofences(
            receiver = receiver,
            geofences = emptyList(),
        )

        verify { geofencingClient.removeGeofences(listOf("home")) }
    }

    @Test
    fun syncGeofences_whenLocationPermissionsMissing_skipsRegistrationAndClearsActiveIds() = runTest {
        every { ContextCompat.checkSelfPermission(any(), any()) } returns PackageManager.PERMISSION_DENIED
        val receiver = receiver()

        setActiveGeofenceIds(receiver, setOf("home"))

        invokeSyncGeofences(
            receiver = receiver,
            geofences = listOf(
                RegisteredGeofence(
                    geofence = sampleGeofence("home"),
                    transitionMask = Geofence.GEOFENCE_TRANSITION_ENTER,
                    notificationResponsivenessMillis = GeofenceUpdateRate.BALANCED.notificationResponsivenessMillis,
                ),
            ),
        )

        verify { geofencingClient.removeGeofences(listOf("home")) }
        verify(exactly = 0) { geofencingClient.removeGeofences(any<PendingIntent>()) }
        verify(exactly = 0) { geofencingClient.addGeofences(any(), any<PendingIntent>()) }
        verify { logger.log(match { it.contains("location permissions are missing") }) }
        assertEquals(emptySet<String>(), activeGeofenceIds(receiver))
    }

    private fun sampleGeofence(id: String): AutomationGeofence = AutomationGeofence(
        id = id,
        name = id.replaceFirstChar { it.uppercase() },
        latitude = 51.0,
        longitude = 4.0,
        radiusMeters = 150f,
        address = null,
    )

    private fun immediateTask(): Task<Void> = mockk(relaxed = true)

    private fun immediateCompleteTask(): Task<Void> {
        val task = mockk<Task<Void>>(relaxed = true)
        every { task.addOnCompleteListener(any<OnCompleteListener<Void>>()) } answers {
            firstArg<OnCompleteListener<Void>>().onComplete(task)
            task
        }
        return task
    }

    private fun immediateSuccessTask(): Task<Void> {
        val task = mockk<Task<Void>>(relaxed = true)
        every { task.addOnSuccessListener(any<OnSuccessListener<in Void>>()) } answers {
            firstArg<OnSuccessListener<Void>>().onSuccess(null)
            task
        }
        every { task.addOnFailureListener(any<OnFailureListener>()) } returns task
        return task
    }

    private fun invokeSyncGeofences(receiver: GeofenceRegistrationReceiver, geofences: List<RegisteredGeofence>) {
        val method = GeofenceRegistrationReceiver::class.java.getDeclaredMethod(
            "syncGeofences",
            List::class.java,
        )
        method.isAccessible = true
        method.invoke(receiver, geofences)
    }

    private fun setActiveGeofenceIds(receiver: GeofenceRegistrationReceiver, ids: Set<String>) {
        val field = GeofenceRegistrationReceiver::class.java.getDeclaredField("activeGeofenceIds")
        field.isAccessible = true
        field.set(receiver, ids)
    }

    @Suppress("UNCHECKED_CAST")
    private fun activeGeofenceIds(receiver: GeofenceRegistrationReceiver): Set<String> {
        val field = GeofenceRegistrationReceiver::class.java.getDeclaredField("activeGeofenceIds")
        field.isAccessible = true
        return field.get(receiver) as Set<String>
    }

    private fun receiver(): GeofenceRegistrationReceiver = GeofenceRegistrationReceiver(
        context = context,
        service = runtimeService,
        scope = scope,
        logger = logger,
    ).also { receiver ->
        val field = GeofenceRegistrationReceiver::class.java.getDeclaredField("geofencingClient")
        field.isAccessible = true
        field.set(receiver, geofencingClient)
    }
}
