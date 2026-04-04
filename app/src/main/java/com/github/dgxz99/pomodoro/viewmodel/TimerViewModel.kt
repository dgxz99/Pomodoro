package com.github.dgxz99.pomodoro.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.github.dgxz99.pomodoro.data.local.PomodoroDatabase
import com.github.dgxz99.pomodoro.data.preferences.SettingsDataStore
import com.github.dgxz99.pomodoro.data.repository.PomodoroRepository
import com.github.dgxz99.pomodoro.domain.model.TimerMode
import com.github.dgxz99.pomodoro.domain.model.TimerState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class TimerViewModel(application: Application) : AndroidViewModel(application) {
    
    private val settingsDataStore = SettingsDataStore(application)
    private val repository = PomodoroRepository(
        PomodoroDatabase.getDatabase(application).pomodoroDao()
    )
    
    private val _timerState = MutableStateFlow(TimerState())
    val timerState: StateFlow<TimerState> = _timerState.asStateFlow()
    
    private val _todayCount = MutableStateFlow(0)
    val todayCount: StateFlow<Int> = _todayCount.asStateFlow()
    
    private val _todayMinutes = MutableStateFlow(0)
    val todayMinutes: StateFlow<Int> = _todayMinutes.asStateFlow()
    
    private var timerJob: Job? = null
    private var focusDuration = SettingsDataStore.DEFAULT_FOCUS_DURATION
    private var shortBreakDuration = SettingsDataStore.DEFAULT_SHORT_BREAK_DURATION
    private var longBreakDuration = SettingsDataStore.DEFAULT_LONG_BREAK_DURATION
    private var pomodorosUntilLongBreak = SettingsDataStore.DEFAULT_POMODOROS_UNTIL_LONG_BREAK
    
    init {
        loadSettings()
        loadTodayStats()
    }
    
    private fun loadSettings() {
        viewModelScope.launch {
            focusDuration = settingsDataStore.focusDuration.first()
            shortBreakDuration = settingsDataStore.shortBreakDuration.first()
            longBreakDuration = settingsDataStore.longBreakDuration.first()
            pomodorosUntilLongBreak = settingsDataStore.pomodorosUntilLongBreak.first()
            
            // Initialize timer with focus duration
            _timerState.value = TimerState(
                mode = TimerMode.FOCUS,
                remainingSeconds = focusDuration * 60,
                isRunning = false,
                currentPomodoroIndex = 0
            )
        }
    }
    
    private fun loadTodayStats() {
        viewModelScope.launch {
            _todayCount.value = repository.getTodayCount()
            // Calculate today's minutes from records
            val startOfDay = getStartOfDayTimestamp()
            repository.getRecordsSince(startOfDay).collect { records ->
                _todayMinutes.value = records.sumOf { it.duration }
                _todayCount.value = records.size
            }
        }
    }
    
    private fun getStartOfDayTimestamp(): Long {
        val calendar = java.util.Calendar.getInstance()
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 0)
        calendar.set(java.util.Calendar.MINUTE, 0)
        calendar.set(java.util.Calendar.SECOND, 0)
        calendar.set(java.util.Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }
    
    fun start() {
        if (_timerState.value.isRunning) return
        
        _timerState.value = _timerState.value.copy(isRunning = true)
        
        timerJob = viewModelScope.launch {
            while (_timerState.value.remainingSeconds > 0 && _timerState.value.isRunning) {
                delay(1000)
                _timerState.value = _timerState.value.copy(
                    remainingSeconds = _timerState.value.remainingSeconds - 1
                )
            }
            
            if (_timerState.value.remainingSeconds == 0) {
                onTimerComplete()
            }
        }
    }
    
    fun pause() {
        timerJob?.cancel()
        _timerState.value = _timerState.value.copy(isRunning = false)
    }
    
    fun stop() {
        timerJob?.cancel()
        val currentMode = _timerState.value.mode
        val duration = when (currentMode) {
            TimerMode.FOCUS -> focusDuration
            TimerMode.SHORT_BREAK -> shortBreakDuration
            TimerMode.LONG_BREAK -> longBreakDuration
        }
        _timerState.value = _timerState.value.copy(
            remainingSeconds = duration * 60,
            isRunning = false
        )
    }
    
    private fun onTimerComplete() {
        viewModelScope.launch {
            val currentState = _timerState.value
            
            when (currentState.mode) {
                TimerMode.FOCUS -> {
                    // Record completed pomodoro
                    repository.insertRecord(focusDuration)
                    
                    val newIndex = currentState.currentPomodoroIndex + 1
                    
                    if (newIndex >= pomodorosUntilLongBreak) {
                        // Switch to long break
                        _timerState.value = TimerState(
                            mode = TimerMode.LONG_BREAK,
                            remainingSeconds = longBreakDuration * 60,
                            isRunning = false,
                            currentPomodoroIndex = 0
                        )
                    } else {
                        // Switch to short break
                        _timerState.value = currentState.copy(
                            mode = TimerMode.SHORT_BREAK,
                            remainingSeconds = shortBreakDuration * 60,
                            isRunning = false,
                            currentPomodoroIndex = newIndex
                        )
                    }
                }
                
                TimerMode.SHORT_BREAK, TimerMode.LONG_BREAK -> {
                    // Switch back to focus
                    _timerState.value = currentState.copy(
                        mode = TimerMode.FOCUS,
                        remainingSeconds = focusDuration * 60,
                        isRunning = false
                    )
                }
            }
        }
    }
    
    fun refreshSettings() {
        loadSettings()
    }
    
    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
