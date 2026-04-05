package com.tomtruyen.automation.features.actions

import android.content.Context
import com.tomtruyen.automation.core.event.AutomationEvent
import com.tomtruyen.automation.core.event.BatteryChangedEvent
import com.tomtruyen.automation.core.model.BatteryChargeState
import com.tomtruyen.automation.core.model.BatteryPlugStatus
import com.tomtruyen.automation.features.actions.config.ActionConfig
import com.tomtruyen.automation.features.actions.config.LogMessageActionConfig
import com.tomtruyen.automation.features.actions.delegate.ActionDelegate
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

internal class ActionExecutorTest {
    @MockK
    private lateinit var context: Context

    @MockK
    private lateinit var delegate: ActionDelegate<LogMessageActionConfig>

    private lateinit var action: LogMessageActionConfig
    private lateinit var event: AutomationEvent

    @Before
    fun setUp() {
        MockKAnnotations.init(this)

        action = LogMessageActionConfig(message = "hello")
        event = BatteryChangedEvent(
            level = 50,
            scale = 100,
            chargeState = BatteryChargeState.CHARGING,
            plugStatus = BatteryPlugStatus.AC
        )

        coEvery { delegate.type } returns ActionType.LOG_MESSAGE
        coEvery { delegate.execute(any(), any()) } returns Unit
    }

    @Test
    fun executeAll_executesEveryActionWithMatchingDelegate() = runTest {
        val secondAction = LogMessageActionConfig(message = "second")
        val executor = ActionExecutor(context, listOf(delegate))

        executor.executeAll(listOf(action, secondAction), event)

        coVerify { delegate.execute(action, event) }
        coVerify { delegate.execute(secondAction, event) }
    }

    @Test
    fun executeAll_whenDelegateDoesNotExist_skipsAction() = runTest {
        val executor = ActionExecutor(context, emptyList())

        executor.executeAll(listOf(action), event)

        coVerify(exactly = 0) { delegate.execute(any(), any()) }
    }

    @Test
    fun executeAll_whenActionListIsEmpty_doesNothing() = runTest {
        val executor = ActionExecutor(context, listOf(delegate))

        executor.executeAll(emptyList(), event)

        coVerify(exactly = 0) { delegate.execute(any(), any()) }
    }
}
