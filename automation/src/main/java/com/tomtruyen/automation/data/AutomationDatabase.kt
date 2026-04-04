package com.tomtruyen.automation.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.tomtruyen.automation.data.dao.AutomationRuleDao
import com.tomtruyen.automation.data.entity.AutomationRuleEntity

@Database(
    entities = [AutomationRuleEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AutomationDatabase : RoomDatabase() {
    abstract fun automationRuleDao(): AutomationRuleDao
}