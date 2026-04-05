package com.tomtruyen.automation.data.migration

import android.content.Context
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class SqlAssetMigration(
    private val context: Context,
    startVersion: Int,
    endVersion: Int,
    private val assetPath: String,
) : Migration(startVersion, endVersion) {
    override fun migrate(db: SupportSQLiteDatabase) {
        context.assets.open(assetPath).bufferedReader().use { reader ->
            reader.readText()
                .splitToSequence(';')
                .map(String::trim)
                .filter(String::isNotEmpty)
                .forEach(db::execSQL)
        }
    }
}
