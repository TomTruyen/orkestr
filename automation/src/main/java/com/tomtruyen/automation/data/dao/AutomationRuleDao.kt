package com.tomtruyen.automation.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tomtruyen.automation.data.entity.AutomationRuleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AutomationRuleDao {
    @Query("SELECT * FROM automation_rules ORDER BY updatedAtEpochMillis DESC, name ASC")
    fun observeAll(): Flow<List<AutomationRuleEntity>>

    @Query("SELECT * FROM automation_rules WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): AutomationRuleEntity?

    @Query("SELECT * FROM automation_rules WHERE enabled = 1")
    suspend fun getEnabled(): List<AutomationRuleEntity>

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun upsert(rule: AutomationRuleEntity)

    @Query("DELETE FROM automation_rules WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("UPDATE automation_rules SET enabled = :enabled, updatedAtEpochMillis = :updatedAtEpochMillis WHERE id = :id")
    suspend fun updateEnabled(id: String, enabled: Boolean, updatedAtEpochMillis: Long)
}