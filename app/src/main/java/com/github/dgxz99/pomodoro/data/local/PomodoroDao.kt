package com.github.dgxz99.pomodoro.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PomodoroDao {
    @Insert
    suspend fun insert(record: PomodoroRecord)

    @Query("SELECT * FROM pomodoro_records ORDER BY completedAt DESC")
    fun getAllRecords(): Flow<List<PomodoroRecord>>

    @Query("SELECT * FROM pomodoro_records WHERE completedAt >= :startTimestamp ORDER BY completedAt DESC")
    fun getRecordsSince(startTimestamp: Long): Flow<List<PomodoroRecord>>

    @Query("SELECT COUNT(*) FROM pomodoro_records WHERE completedAt >= :startTimestamp")
    suspend fun getCountSince(startTimestamp: Long): Int

    @Query("SELECT COUNT(*) FROM pomodoro_records WHERE completedAt >= :startTimestamp AND completedAt < :endTimestamp")
    suspend fun getCountForDay(startTimestamp: Long, endTimestamp: Long): Int

    @Query("DELETE FROM pomodoro_records")
    suspend fun deleteAll()
}
