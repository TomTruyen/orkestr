package com.tomtruyen.automation.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tomtruyen.automation.data.entity.GeofenceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GeofenceDao {
    @Query("SELECT * FROM geofences ORDER BY updatedAtEpochMillis DESC, name ASC")
    fun observeAll(): Flow<List<GeofenceEntity>>

    @Query("SELECT * FROM geofences WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): GeofenceEntity?

    @Query("SELECT * FROM geofences WHERE id IN (:ids)")
    suspend fun getByIds(ids: List<String>): List<GeofenceEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: GeofenceEntity)

    @Query("DELETE FROM geofences WHERE id = :id")
    suspend fun deleteById(id: String)
}
