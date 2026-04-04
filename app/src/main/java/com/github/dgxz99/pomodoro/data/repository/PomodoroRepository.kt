package com.github.dgxz99.pomodoro.data.repository

import com.github.dgxz99.pomodoro.data.local.PomodoroDao
import com.github.dgxz99.pomodoro.data.local.PomodoroRecord
import kotlinx.coroutines.flow.Flow
import java.util.Calendar

class PomodoroRepository(private val pomodoroDao: PomodoroDao) {
    
    fun getAllRecords(): Flow<List<PomodoroRecord>> {
        return pomodoroDao.getAllRecords()
    }
    
    suspend fun insertRecord(duration: Int) {
        val record = PomodoroRecord(
            completedAt = System.currentTimeMillis(),
            duration = duration
        )
        pomodoroDao.insert(record)
    }
    
    suspend fun getTodayCount(): Int {
        val startOfDay = getStartOfDayTimestamp()
        return pomodoroDao.getCountSince(startOfDay)
    }
    
    suspend fun getWeekCount(): Int {
        val startOfWeek = getStartOfWeekTimestamp()
        return pomodoroDao.getCountSince(startOfWeek)
    }
    
    suspend fun getMonthCount(): Int {
        val startOfMonth = getStartOfMonthTimestamp()
        return pomodoroDao.getCountSince(startOfMonth)
    }
    
    suspend fun getTotalCount(): Int {
        return pomodoroDao.getCountSince(0)
    }
    
    fun getRecordsSince(timestamp: Long): Flow<List<PomodoroRecord>> {
        return pomodoroDao.getRecordsSince(timestamp)
    }
    
    private fun getStartOfDayTimestamp(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }
    
    private fun getStartOfWeekTimestamp(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }
    
    private fun getStartOfMonthTimestamp(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }
}
