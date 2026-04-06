package com.tomtruyen.automation.data.repository

import com.tomtruyen.automation.core.AutomationRule
import com.tomtruyen.automation.data.dao.AutomationRuleDao
import com.tomtruyen.automation.data.entity.AutomationRuleEntity
import com.tomtruyen.automation.features.actions.ActionExecutionMode
import com.tomtruyen.automation.features.actions.config.LogMessageActionConfig
import com.tomtruyen.automation.features.constraints.config.BatteryLevelConstraintConfig
import com.tomtruyen.automation.features.triggers.config.BatteryChangedTriggerConfig
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

internal class AutomationRuleRepositoryTest {
    @MockK
    private lateinit var dao: AutomationRuleDao

    private lateinit var repository: AutomationRuleRepository

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        repository = AutomationRuleRepositoryImpl(dao)
    }

    @Test
    fun observeRules_mapsEntitiesToDomainModels() = runTest {
        val entity = sampleEntity(enabled = true)
        every { dao.observeAll() } returns MutableStateFlow(listOf(entity))

        val result = repository.observeRules().first()

        assertEquals(listOf(sampleRule(enabled = true)), result)
    }

    @Test
    fun getRule_whenDaoReturnsNull_returnsNull() = runTest {
        coEvery { dao.getById("missing") } returns null

        val result = repository.getRule("missing")

        assertNull(result)
    }

    @Test
    fun getEnabledRules_mapsEntitiesToDomainModels() = runTest {
        coEvery { dao.getEnabled() } returns listOf(sampleEntity(enabled = true))

        val result = repository.getEnabledRules()

        assertEquals(listOf(sampleRule(enabled = true)), result)
    }

    @Test
    fun upsertRule_mapsDomainModelToEntity() = runTest {
        val slot = slot<AutomationRuleEntity>()
        coEvery { dao.upsert(capture(slot)) } returns Unit
        val before = System.currentTimeMillis()

        repository.upsertRule(sampleRule())

        val after = System.currentTimeMillis()
        coVerify { dao.upsert(any()) }
        assertEquals("rule-id", slot.captured.id)
        assertEquals("Rule", slot.captured.name)
        assertEquals(listOf(BatteryChangedTriggerConfig()), slot.captured.triggers)
        assertEquals(ActionExecutionMode.PARALLEL.name, slot.captured.actionExecutionMode)
        assertTrue(slot.captured.updatedAtEpochMillis in before..after)
    }

    @Test
    fun deleteRule_delegatesToDao() = runTest {
        coEvery { dao.deleteById("rule-id") } returns Unit

        repository.deleteRule("rule-id")

        coVerify { dao.deleteById("rule-id") }
    }

    @Test
    fun updateEnabled_forwardsStateAndGeneratedTimestamp() = runTest {
        val timestampSlot = slot<Long>()
        coEvery {
            dao.updateEnabled(
                id = "rule-id",
                enabled = false,
                updatedAtEpochMillis = capture(timestampSlot),
            )
        } returns Unit
        val before = System.currentTimeMillis()

        repository.updateEnabled("rule-id", false)

        val after = System.currentTimeMillis()
        coVerify { dao.updateEnabled("rule-id", false, any()) }
        assertTrue(timestampSlot.captured in before..after)
    }

    private fun sampleRule(enabled: Boolean = false): AutomationRule = AutomationRule(
        id = "rule-id",
        name = "Rule",
        enabled = enabled,
        triggers = listOf(BatteryChangedTriggerConfig()),
        constraints = listOf(BatteryLevelConstraintConfig()),
        actions = listOf(LogMessageActionConfig()),
        actionExecutionMode = ActionExecutionMode.PARALLEL,
    )

    private fun sampleEntity(enabled: Boolean = false): AutomationRuleEntity = AutomationRuleEntity(
        id = "rule-id",
        name = "Rule",
        enabled = enabled,
        triggers = listOf(BatteryChangedTriggerConfig()),
        constraints = listOf(BatteryLevelConstraintConfig()),
        actions = listOf(LogMessageActionConfig()),
        actionExecutionMode = ActionExecutionMode.PARALLEL.name,
        updatedAtEpochMillis = 123L,
    )
}
