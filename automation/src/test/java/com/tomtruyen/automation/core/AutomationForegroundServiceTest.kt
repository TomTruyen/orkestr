package com.tomtruyen.automation.core

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.ResolveInfo
import androidx.core.content.ContextCompat
import com.tomtruyen.automation.core.AutomationForegroundService.Companion.start
import com.tomtruyen.automation.data.repository.AutomationRuleRepository
import com.tomtruyen.automation.features.triggers.config.BatteryChangedTriggerConfig
import com.tomtruyen.automation.features.triggers.receiver.TriggerReceiver
import com.tomtruyen.automation.features.triggers.receiver.TriggerReceiverKey
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf

@RunWith(RobolectricTestRunner::class)
internal class AutomationForegroundServiceTest {
    @MockK
    private lateinit var runtimeService: AutomationRuntimeService

    @MockK
    private lateinit var repository: AutomationRuleRepository

    @MockK
    private lateinit var logger: AutomationLogger

    @MockK
    private lateinit var context: Context

    private lateinit var firstReceiver: TriggerReceiver
    private lateinit var secondReceiver: TriggerReceiver
    private lateinit var firstFactory: TriggerReceiver.TriggerFactory
    private lateinit var secondFactory: TriggerReceiver.TriggerFactory
    private lateinit var rulesFlow: MutableSharedFlow<List<AutomationRule>>

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        rulesFlow = MutableSharedFlow(replay = 1)
        every { repository.observeRules() } returns rulesFlow

        firstReceiver = mockk(relaxed = true)
        secondReceiver = mockk(relaxed = true)
        firstFactory = factory(firstReceiver)
        secondFactory = factory(secondReceiver)

        stopKoin()
        startKoin {
            modules(
                module {
                    single<AutomationRuntimeService> { this@AutomationForegroundServiceTest.runtimeService }
                    single<AutomationRuleRepository> { this@AutomationForegroundServiceTest.repository }
                    single<AutomationLogger> { this@AutomationForegroundServiceTest.logger }
                    single<List<TriggerReceiver.TriggerFactory>> { listOf(firstFactory, secondFactory) }
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
    fun onStartCommand_whenStopActionIsReceived_returnsNotSticky() {
        val service = Robolectric.buildService(AutomationForegroundService::class.java).create().get()

        val result = service.onStartCommand(
            Intent().apply {
                action = "com.tomtruyen.automation.action.STOP_FOREGROUND_SERVICE"
            },
            0,
            0,
        )

        assertEquals(Service.START_NOT_STICKY, result)
    }

    @Test
    fun onStartCommand_whenActionIsDifferent_returnsSticky() {
        val service = Robolectric.buildService(AutomationForegroundService::class.java).create().get()

        val result = service.onStartCommand(Intent("other"), 0, 0)

        assertEquals(Service.START_STICKY, result)
    }

    @Test
    fun start_buildsForegroundServiceIntent() {
        mockkStatic(ContextCompat::class)
        every { ContextCompat.startForegroundService(context, any()) } returns mockk()
        every { context.packageName } returns "com.tomtruyen.automation.test"

        start(context)

        verify {
            ContextCompat.startForegroundService(
                context,
                match {
                    it.component?.className == AutomationForegroundService::class.java.name &&
                        it.action == "com.tomtruyen.automation.action.START_FOREGROUND_SERVICE"
                },
            )
        }
    }

    @Test
    fun syncReceivers_registersAndUnregistersFactoriesBasedOnActiveKeys() {
        val service = Robolectric.buildService(AutomationForegroundService::class.java).create().get()

        invokeSyncReceivers(service, setOf(TriggerReceiverKey.BATTERY_CHANGED))
        assertEquals(2, registeredReceivers(service).size)

        invokeSyncReceivers(service, emptySet())

        assertTrue(registeredReceivers(service).isEmpty())
    }

    @Test
    fun onDestroy_clearsRegisteredReceivers() {
        val service = Robolectric.buildService(AutomationForegroundService::class.java).create().get()
        invokeSyncReceivers(service, setOf(TriggerReceiverKey.BATTERY_CHANGED))

        service.onDestroy()

        assertTrue(registeredReceivers(service).isEmpty())
    }

    @Test
    fun onCreate_observesEnabledRulesAndRegistersRequiredReceivers() {
        val service = Robolectric.buildService(AutomationForegroundService::class.java).create().get()

        runBlocking {
            rulesFlow.emit(
                listOf(
                    AutomationRule(
                        id = "disabled",
                        name = "Disabled rule",
                        enabled = false,
                        triggers = listOf(BatteryChangedTriggerConfig()),
                        constraints = emptyList(),
                        actions = emptyList(),
                    ),
                    AutomationRule(
                        id = "enabled",
                        name = "Enabled rule",
                        enabled = true,
                        triggers = listOf(BatteryChangedTriggerConfig()),
                        constraints = emptyList(),
                        actions = emptyList(),
                    ),
                ),
            )
        }

        waitUntil { registeredReceivers(service).size == 2 }

        assertEquals(2, registeredReceivers(service).size)
    }

    @Test
    fun buildNotification_whenLaunchIntentExists_setsContentIntent() {
        val controller = Robolectric.buildService(AutomationForegroundService::class.java)
        val service = controller.get()
        val launchProbeIntent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
            setPackage(service.packageName)
        }
        val resolveInfo = ResolveInfo().apply {
            activityInfo = ActivityInfo().apply {
                packageName = service.packageName
                name = "com.tomtruyen.automation.TestLauncherActivity"
            }
        }
        shadowOf(service.packageManager).addResolveInfoForIntent(launchProbeIntent, resolveInfo)

        val notification = invokeBuildNotification(service)

        assertNotNull(notification.contentIntent)
    }

    private fun factory(receiver: TriggerReceiver): TriggerReceiver.TriggerFactory =
        object : TriggerReceiver.TriggerFactory {
            override val key: TriggerReceiverKey = TriggerReceiverKey.BATTERY_CHANGED

            override fun register(
                context: Context,
                service: AutomationRuntimeService,
                scope: kotlinx.coroutines.CoroutineScope,
                logger: AutomationLogger,
            ): TriggerReceiver = receiver
        }

    @Suppress("UNCHECKED_CAST")
    private fun registeredReceivers(
        service: AutomationForegroundService,
    ): MutableMap<TriggerReceiver.TriggerFactory, TriggerReceiver> {
        val field = AutomationForegroundService::class.java.getDeclaredField("registeredReceivers")
        field.isAccessible = true
        return field.get(service) as MutableMap<TriggerReceiver.TriggerFactory, TriggerReceiver>
    }

    private fun invokeSyncReceivers(service: AutomationForegroundService, keys: Set<TriggerReceiverKey>) {
        val method = AutomationForegroundService::class.java.getDeclaredMethod("syncReceivers", Set::class.java)
        method.isAccessible = true
        method.invoke(service, keys)
    }

    private fun invokeBuildNotification(service: AutomationForegroundService): android.app.Notification {
        val method = AutomationForegroundService::class.java.getDeclaredMethod("buildNotification")
        method.isAccessible = true
        return method.invoke(service) as android.app.Notification
    }

    private fun waitUntil(timeoutMs: Long = 2_000, condition: () -> Boolean) {
        val deadline = System.currentTimeMillis() + timeoutMs
        while (!condition() && System.currentTimeMillis() < deadline) {
            Thread.sleep(10)
        }
        check(condition()) { "Condition was not met within ${timeoutMs}ms" }
    }
}
