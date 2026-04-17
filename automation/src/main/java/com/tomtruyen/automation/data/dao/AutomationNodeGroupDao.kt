package com.tomtruyen.automation.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tomtruyen.automation.data.entity.AutomationNodeGroupEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AutomationNodeGroupDao {
    @Query("SELECT * FROM automation_node_groups ORDER BY updatedAtEpochMillis DESC, name ASC")
    fun observeAll(): Flow<List<AutomationNodeGroupEntity>>

    @Query("SELECT * FROM automation_node_groups WHERE type = :type ORDER BY updatedAtEpochMillis DESC, name ASC")
    fun observeByType(type: String): Flow<List<AutomationNodeGroupEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(group: AutomationNodeGroupEntity)

    @Query("DELETE FROM automation_node_groups WHERE id = :id")
    suspend fun deleteById(id: String)
}
