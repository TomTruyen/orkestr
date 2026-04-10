package com.tomtruyen.automation.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.tomtruyen.automation.data.dao.AutomationLogDao
import com.tomtruyen.automation.data.dao.AutomationRuleDao
import com.tomtruyen.automation.data.dao.GeofenceDao
import com.tomtruyen.automation.data.entity.AutomationLogEntity
import com.tomtruyen.automation.data.entity.AutomationRuleEntity
import com.tomtruyen.automation.data.entity.GeofenceEntity

@Database(
    entities = [AutomationRuleEntity::class, GeofenceEntity::class, AutomationLogEntity::class],
    version = 5,
    exportSchema = false,
)
@TypeConverters(AutomationRuleTypeConverters::class)
abstract class AutomationDatabase : RoomDatabase() {
    abstract fun automationRuleDao(): AutomationRuleDao
    abstract fun geofenceDao(): GeofenceDao
    abstract fun automationLogDao(): AutomationLogDao
}
