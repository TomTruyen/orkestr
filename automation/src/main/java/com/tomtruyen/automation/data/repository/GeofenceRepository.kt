package com.tomtruyen.automation.data.repository

import com.tomtruyen.automation.core.model.AutomationGeofence
import com.tomtruyen.automation.data.dao.GeofenceDao
import com.tomtruyen.automation.data.entity.GeofenceEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface GeofenceRepository {
    fun observeGeofences(): Flow<List<AutomationGeofence>>
    suspend fun getGeofence(id: String): AutomationGeofence?
    suspend fun getGeofences(ids: Collection<String>): List<AutomationGeofence>
    suspend fun upsertGeofence(geofence: AutomationGeofence)
    suspend fun deleteGeofence(id: String)
}

class GeofenceRepositoryImpl(private val dao: GeofenceDao) : GeofenceRepository {
    override fun observeGeofences(): Flow<List<AutomationGeofence>> = dao.observeAll().map { items ->
        items.map(GeofenceEntity::toDomain)
    }

    override suspend fun getGeofence(id: String): AutomationGeofence? = dao.getById(id)?.toDomain()

    override suspend fun getGeofences(ids: Collection<String>): List<AutomationGeofence> = if (ids.isEmpty()) {
        emptyList()
    } else {
        dao.getByIds(ids.distinct()).map(GeofenceEntity::toDomain)
    }

    override suspend fun upsertGeofence(geofence: AutomationGeofence) {
        dao.upsert(geofence.toEntity())
    }

    override suspend fun deleteGeofence(id: String) {
        dao.deleteById(id)
    }
}

private fun GeofenceEntity.toDomain(): AutomationGeofence = AutomationGeofence(
    id = id,
    name = name,
    latitude = latitude,
    longitude = longitude,
    radiusMeters = radiusMeters,
    address = address,
)

private fun AutomationGeofence.toEntity(): GeofenceEntity = GeofenceEntity(
    id = id,
    name = name,
    latitude = latitude,
    longitude = longitude,
    radiusMeters = radiusMeters,
    address = address,
    updatedAtEpochMillis = System.currentTimeMillis(),
)
