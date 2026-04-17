package com.tomtruyen.automation.data.repository

import com.tomtruyen.automation.core.AutomationNodeGroup
import com.tomtruyen.automation.core.AutomationNodeGroupType
import com.tomtruyen.automation.data.dao.AutomationNodeGroupDao
import com.tomtruyen.automation.data.entity.AutomationNodeGroupEntity
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
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

internal class AutomationNodeGroupRepositoryTest {
    @MockK
    private lateinit var dao: AutomationNodeGroupDao

    private lateinit var repository: AutomationNodeGroupRepository

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        repository = AutomationNodeGroupRepositoryImpl(dao)
    }

    @Test
    fun observeGroups_mapsEntitiesToDomainModels() = runTest {
        every { dao.observeAll() } returns MutableStateFlow(listOf(sampleEntity()))

        val result = repository.observeGroups().first()

        assertEquals(listOf(sampleGroup()), result)
    }

    @Test
    fun observeGroupsByType_filtersThroughDao() = runTest {
        every {
            dao.observeByType(
                AutomationNodeGroupType.TRIGGER.name,
            )
        } returns MutableStateFlow(listOf(sampleEntity()))

        val result = repository.observeGroups(AutomationNodeGroupType.TRIGGER).first()

        assertEquals(listOf(sampleGroup()), result)
    }

    @Test
    fun upsertGroup_mapsDomainModelToEntity() = runTest {
        val slot = slot<AutomationNodeGroupEntity>()
        coEvery { dao.upsert(capture(slot)) } returns Unit
        val before = System.currentTimeMillis()

        repository.upsertGroup(sampleGroup())

        val after = System.currentTimeMillis()
        coVerify { dao.upsert(any()) }
        assertEquals("group-id", slot.captured.id)
        assertEquals("Morning setup", slot.captured.name)
        assertEquals(AutomationNodeGroupType.TRIGGER.name, slot.captured.type)
        assertEquals(listOf(BatteryChangedTriggerConfig()), slot.captured.triggers)
        assertTrue(slot.captured.updatedAtEpochMillis in before..after)
    }

    @Test
    fun deleteGroup_delegatesToDao() = runTest {
        coEvery { dao.deleteById("group-id") } returns Unit

        repository.deleteGroup("group-id")

        coVerify { dao.deleteById("group-id") }
    }

    private fun sampleGroup(): AutomationNodeGroup = AutomationNodeGroup(
        id = "group-id",
        name = "Morning setup",
        type = AutomationNodeGroupType.TRIGGER,
        triggers = listOf(BatteryChangedTriggerConfig()),
        constraints = listOf(BatteryLevelConstraintConfig()),
        actions = listOf(LogMessageActionConfig()),
    )

    private fun sampleEntity(): AutomationNodeGroupEntity = AutomationNodeGroupEntity(
        id = "group-id",
        name = "Morning setup",
        type = AutomationNodeGroupType.TRIGGER.name,
        triggers = listOf(BatteryChangedTriggerConfig()),
        constraints = listOf(BatteryLevelConstraintConfig()),
        actions = listOf(LogMessageActionConfig()),
        updatedAtEpochMillis = 123L,
    )
}
