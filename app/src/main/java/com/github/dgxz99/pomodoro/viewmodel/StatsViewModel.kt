package com.github.dgxz99.pomodoro.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.github.dgxz99.pomodoro.data.local.PomodoroDatabase
import com.github.dgxz99.pomodoro.data.repository.PomodoroRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar

class StatsViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository = PomodoroRepository(
        PomodoroDatabase.getDatabase(application).pomodoroDao()
    )
    
    private val _todayCount = MutableStateFlow(0)
    val todayCount: StateFlow<Int> = _todayCount.asStateFlow()
    
    private val _weekCount = MutableStateFlow(0)
    val weekCount: StateFlow<Int> = _weekCount.asStateFlow()
    
    private val _monthCount = MutableStateFlow(0)
    val monthCount: StateFlow<Int> = _monthCount.asStateFlow()
    
    private val _totalCount = MutableStateFlow(0)
    val totalCount: StateFlow<Int> = _totalCount.asStateFlow()
    
    private val _streakDays = MutableStateFlow(0)
    val streakDays: StateFlow<Int> = _streakDays.asStateFlow()
    
    private val _todayMinutes = MutableStateFlow(0)
    val todayMinutes: StateFlow<Int> = _todayMinutes.asStateFlow()
    
    init {
        loadStats()
    }
    
    private fun loadStats() {
        viewModelScope.launch {
            _todayCount.value = repository.getTodayCount()
            _weekCount.value = repository.getWeekCount()
            _monthCount.value = repository.getMonthCount()
            _totalCount.value = repository.getTotalCount()
            
            // Calculate streak days
            _streakDays.value = calculateStreakDays()
            
            // Calculate today's minutes
            val startOfDay = getStartOfDayTimestamp()
            repository.getRecordsSince(startOfDay).collect { records ->
                _todayMinutes.value = records.sumOf { it.duration }
                _todayCount.value = records.size
            }
        }
    }
    
    private suspend fun calculateStreakDays(): Int {
        // Get all records sorted by date
        var streak = 0
        val calendar = Calendar.getInstance()
        
        // Start from today
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        
        while (true) {
            val dayStart = calendar.timeInMillis
            calendar.add(Calendar.DAY_OF_YEAR, 1)
            val dayEnd = calendar.timeInMillis
            calendar.add(Calendar.DAY_OF_YEAR, -1) // Reset
            
            // Check if there are any records for this day
            val count = repository.getCountForDay(dayStart, dayEnd)
            
            if (count > 0) {
                streak++
                // Move to previous day
                calendar.add(Calendar.DAY_OF_YEAR, -1)
            } else {
                // If it's today and there's no record yet, still continue checking yesterday
                if (streak == 0) {
                    calendar.add(Calendar.DAY_OF_YEAR, -1)
                } else {
                    break
                }
            }
            
            // Safety limit to prevent infinite loop
            if (streak > 365) break
        }
        
        return streak
    }
    
    private fun getStartOfDayTimestamp(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }
    
    fun refresh() {
        loadStats()
    }
}
