package com.tomtruyen.automation.data.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tomtruyen.automation.data.entity.AutomationLogEntity

@Dao
interface AutomationLogDao {
    @Query(
        """
        SELECT * FROM automation_logs
        WHERE :query = ''
            OR LOWER(message) LIKE '%' || :query || '%'
            OR LOWER(IFNULL(stackTrace, '')) LIKE '%' || :query || '%'
        ORDER BY
            CASE WHEN :sort = 'NEWEST_FIRST' THEN timestampEpochMillis END DESC,
            CASE WHEN :sort = 'NEWEST_FIRST' THEN id END DESC,
            CASE WHEN :sort = 'OLDEST_FIRST' THEN timestampEpochMillis END ASC,
            CASE WHEN :sort = 'OLDEST_FIRST' THEN id END ASC,
            CASE WHEN :sort = 'SEVERITY' THEN
                CASE severity
                    WHEN 'ERROR' THEN 4
                    WHEN 'WARNING' THEN 3
                    WHEN 'INFO' THEN 2
                    WHEN 'DEBUG' THEN 1
                    ELSE 0
                END
            END DESC,
            CASE WHEN :sort = 'SEVERITY' THEN timestampEpochMillis END DESC,
            CASE WHEN :sort = 'SEVERITY' THEN id END DESC
        """,
    )
    fun pagingSource(query: String, sort: String): PagingSource<Int, AutomationLogEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(log: AutomationLogEntity)
}
