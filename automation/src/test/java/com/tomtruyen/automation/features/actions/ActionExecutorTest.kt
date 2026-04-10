package com.tomtruyen.automation.features.actions

import android.content.Context
import com.tomtruyen.automation.core.AutomationLogger
import com.tomtruyen.automation.core.event.AutomationEvent
import com.tomtruyen.automation.core.event.BatteryChangedEvent
import com.tomtruyen.automation.core.model.BatteryChargeState
import com.tomtruyen.automation.core.model.BatteryPlugStatus
import com.tomtruyen.automation.features.actions.config.LogMessageActionConfig
import com.tomtruyen.automation.features.actions.config.OpenWebsiteActionConfig
import com.tomtruyen.automation.features.actions.delegate.ActionDelegate
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.runs
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class ActionExecutorTest {
    @MockK
    private lateinit var context: Context

    @MockK
    private lateinit var delegate: ActionDelegate<LogMessageActionConfig>

    @MockK
    private lateinit var secondDelegate: ActionDelegate<OpenWebsiteActionConfig>

    @MockK
    private lateinit var logger: AutomationLogger

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
            plugStatus = BatteryPlugStatus.AC,
        )

        coEvery { delegate.type } returns ActionType.LOG_MESSAGE
        coEvery { delegate.execute(any(), any()) } returns Unit
        coEvery { secondDelegate.type } returns ActionType.OPEN_WEBSITE
        coEvery { secondDelegate.execute(any(), any()) } returns Unit
        every { logger.log(any()) } just runs
        every { logger.log(any(), any()) } just runs
        every { logger.error(any(), any()) } just runs
    }

    @Test
    fun executeAll_executesEveryActionWithMatchingDelegate() = runTest {
        val secondAction = LogMessageActionConfig(message = "second")
        val executor = ActionExecutor(context, listOf(delegate), logger)

        executor.executeAll(listOf(action, secondAction), event)

        coVerify { delegate.execute(action, event) }
        coVerify { delegate.execute(secondAction, event) }
    }

    @Test
    fun executeAll_whenDelegateDoesNotExist_skipsAction() = runTest {
        val executor = ActionExecutor(context, emptyList(), logger)

        executor.executeAll(listOf(action), event)

        coVerify(exactly = 0) { delegate.execute(any(), any()) }
    }

    @Test
    fun executeAll_whenActionListIsEmpty_doesNothing() = runTest {
        val executor = ActionExecutor(context, listOf(delegate), logger)

        executor.executeAll(emptyList(), event)

        coVerify(exactly = 0) { delegate.execute(any(), any()) }
    }

    @Test
    fun executeAll_whenSequential_preservesActionOrder() = runTest {
        val executionOrder = mutableListOf<String>()
        val firstAction = LogMessageActionConfig(message = "first")
        val secondAction = OpenWebsiteActionConfig(url = "https://orkestr.app")
        val executor = ActionExecutor(context, listOf(delegate, secondDelegate), logger)

        coEvery { delegate.execute(firstAction, event) } coAnswers {
            executionOrder += "start-first"
            delay(10)
            executionOrder += "end-first"
        }
        coEvery { secondDelegate.execute(secondAction, event) } coAnswers {
            executionOrder += "start-second"
            executionOrder += "end-second"
        }

        executor.executeAll(
            actions = listOf(firstAction, secondAction),
            event = event,
            executionMode = ActionExecutionMode.SEQUENTIAL,
        )

        assertEquals(
            listOf("start-first", "end-first", "start-second", "end-second"),
            executionOrder,
        )
    }

    @Test
    fun executeAll_whenParallel_startsLaterActionsBeforeEarlierOnesFinish() = runTest {
        val executionOrder = mutableListOf<String>()
        val firstStarted = CompletableDeferred<Unit>()
        val releaseFirst = CompletableDeferred<Unit>()
        val firstAction = LogMessageActionConfig(message = "first")
        val secondAction = OpenWebsiteActionConfig(url = "https://orkestr.app")
        val executor = ActionExecutor(context, listOf(delegate, secondDelegate), logger)

        coEvery { delegate.execute(firstAction, event) } coAnswers {
            executionOrder += "start-first"
            firstStarted.complete(Unit)
            releaseFirst.await()
            executionOrder += "end-first"
        }
        coEvery { secondDelegate.execute(secondAction, event) } coAnswers {
            firstStarted.await()
            executionOrder += "start-second"
            executionOrder += "end-second"
        }

        val job = launch {
            executor.executeAll(
                actions = listOf(firstAction, secondAction),
                event = event,
                executionMode = ActionExecutionMode.PARALLEL,
            )
        }

        firstStarted.await()
        advanceUntilIdle()
        assertEquals(listOf("start-first", "start-second", "end-second"), executionOrder)

        releaseFirst.complete(Unit)
        job.join()
        assertEquals(listOf("start-first", "start-second", "end-second", "end-first"), executionOrder)
    }

    @Test
    fun executeAll_whenParallelAndOneActionFails_continuesOtherActions() = runTest {
        val firstAction = LogMessageActionConfig(message = "first")
        val secondAction = OpenWebsiteActionConfig(url = "https://orkestr.app")
        val failure = IllegalStateException("boom")
        val executor = ActionExecutor(context, listOf(delegate, secondDelegate), logger)

        coEvery { delegate.execute(firstAction, event) } throws failure

        executor.executeAll(
            actions = listOf(firstAction, secondAction),
            event = event,
            executionMode = ActionExecutionMode.PARALLEL,
        )

        coVerify { secondDelegate.execute(secondAction, event) }
        io.mockk.verify {
            logger.error(
                match { it.contains("LOG_MESSAGE") },
                failure,
            )
        }
    }
}
