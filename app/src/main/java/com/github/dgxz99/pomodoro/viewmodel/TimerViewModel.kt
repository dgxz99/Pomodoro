package com.github.dgxz99.pomodoro.viewmodel

import android.app.Application
import android.content.Intent
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.github.dgxz99.pomodoro.data.local.PomodoroDatabase
import com.github.dgxz99.pomodoro.data.preferences.SettingsDataStore
import com.github.dgxz99.pomodoro.data.repository.PomodoroRepository
import com.github.dgxz99.pomodoro.domain.model.TimerMode
import com.github.dgxz99.pomodoro.domain.model.TimerState
import com.github.dgxz99.pomodoro.service.TimerService
import com.github.dgxz99.pomodoro.util.NotificationHelper
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class TimerViewModel(application: Application) : AndroidViewModel(application) {
    
    private val context = application
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
    
    // Expose settings as StateFlow for UI to observe
    private val _focusDuration = MutableStateFlow(SettingsDataStore.DEFAULT_FOCUS_DURATION)
    val focusDuration: StateFlow<Int> = _focusDuration.asStateFlow()
    
    private val _shortBreakDuration = MutableStateFlow(SettingsDataStore.DEFAULT_SHORT_BREAK_DURATION)
    val shortBreakDuration: StateFlow<Int> = _shortBreakDuration.asStateFlow()
    
    private val _longBreakDuration = MutableStateFlow(SettingsDataStore.DEFAULT_LONG_BREAK_DURATION)
    val longBreakDuration: StateFlow<Int> = _longBreakDuration.asStateFlow()
    
    private val _pomodorosUntilLongBreak = MutableStateFlow(SettingsDataStore.DEFAULT_POMODOROS_UNTIL_LONG_BREAK)
    val pomodorosUntilLongBreak: StateFlow<Int> = _pomodorosUntilLongBreak.asStateFlow()
    
    // Ringtone URIs
    private val _focusCompleteRingtoneUri = MutableStateFlow<String?>(null)
    private val _breakCompleteRingtoneUri = MutableStateFlow<String?>(null)
    
    private var timerJob: Job? = null
    
    init {
        NotificationHelper.createNotificationChannels(application)
        loadSettings()
        loadTodayStats()
    }
    
    private fun loadSettings() {
        viewModelScope.launch {
            _focusDuration.value = settingsDataStore.focusDuration.first()
            _shortBreakDuration.value = settingsDataStore.shortBreakDuration.first()
            _longBreakDuration.value = settingsDataStore.longBreakDuration.first()
            _pomodorosUntilLongBreak.value = settingsDataStore.pomodorosUntilLongBreak.first()
            
            // Load ringtone settings
            _focusCompleteRingtoneUri.value = settingsDataStore.focusCompleteRingtone.first()
            _breakCompleteRingtoneUri.value = settingsDataStore.breakCompleteRingtone.first()
            
            // Initialize timer with focus duration
            _timerState.value = TimerState(
                mode = TimerMode.FOCUS,
                remainingSeconds = _focusDuration.value * 60,
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
        
        // Start foreground service for background timing
        startTimerService()
        
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
        stopTimerService()
    }
    
    fun stop() {
        timerJob?.cancel()
        val currentMode = _timerState.value.mode
        val duration = when (currentMode) {
            TimerMode.FOCUS -> _focusDuration.value
            TimerMode.SHORT_BREAK -> _shortBreakDuration.value
            TimerMode.LONG_BREAK -> _longBreakDuration.value
        }
        _timerState.value = _timerState.value.copy(
            remainingSeconds = duration * 60,
            isRunning = false
        )
        stopTimerService()
    }
    
    private fun onTimerComplete() {
        viewModelScope.launch {
            val currentState = _timerState.value
            
            // Determine which ringtone to use based on mode
            val ringtoneUri = when (currentState.mode) {
                TimerMode.FOCUS -> _focusCompleteRingtoneUri.value
                TimerMode.SHORT_BREAK, TimerMode.LONG_BREAK -> _breakCompleteRingtoneUri.value
            }
            
            // Show completion notification with custom ringtone
            NotificationHelper.showTimerCompleteNotification(context, currentState.mode, ringtoneUri)
            
            when (currentState.mode) {
                TimerMode.FOCUS -> {
                    // Record completed pomodoro
                    repository.insertRecord(_focusDuration.value)
                    
                    val newIndex = currentState.currentPomodoroIndex + 1
                    
                    if (newIndex >= _pomodorosUntilLongBreak.value) {
                        // Switch to long break
                        _timerState.value = TimerState(
                            mode = TimerMode.LONG_BREAK,
                            remainingSeconds = _longBreakDuration.value * 60,
                            isRunning = false,
                            currentPomodoroIndex = 0
                        )
                    } else {
                        // Switch to short break
                        _timerState.value = currentState.copy(
                            mode = TimerMode.SHORT_BREAK,
                            remainingSeconds = _shortBreakDuration.value * 60,
                            isRunning = false,
                            currentPomodoroIndex = newIndex
                        )
                    }
                }
                
                TimerMode.SHORT_BREAK, TimerMode.LONG_BREAK -> {
                    // Switch back to focus
                    _timerState.value = currentState.copy(
                        mode = TimerMode.FOCUS,
                        remainingSeconds = _focusDuration.value * 60,
                        isRunning = false
                    )
                }
            }
            
            stopTimerService()
        }
    }
    
    private fun startTimerService() {
        val intent = Intent(context, TimerService::class.java).apply {
            action = TimerService.ACTION_START
            putExtra(TimerService.EXTRA_SECONDS, _timerState.value.remainingSeconds)
            putExtra(TimerService.EXTRA_MODE, _timerState.value.mode.name)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }
    }
    
    private fun stopTimerService() {
        val intent = Intent(context, TimerService::class.java).apply {
            action = TimerService.ACTION_STOP
        }
        context.startService(intent)
    }
    
    fun refreshSettings() {
        viewModelScope.launch {
            _focusDuration.value = settingsDataStore.focusDuration.first()
            _shortBreakDuration.value = settingsDataStore.shortBreakDuration.first()
            _longBreakDuration.value = settingsDataStore.longBreakDuration.first()
            _pomodorosUntilLongBreak.value = settingsDataStore.pomodorosUntilLongBreak.first()
            
            // Refresh ringtone settings
            _focusCompleteRingtoneUri.value = settingsDataStore.focusCompleteRingtone.first()
            _breakCompleteRingtoneUri.value = settingsDataStore.breakCompleteRingtone.first()
            
            // Update timer immediately if not running
            if (!_timerState.value.isRunning) {
                val currentMode = _timerState.value.mode
                val duration = when (currentMode) {
                    TimerMode.FOCUS -> _focusDuration.value
                    TimerMode.SHORT_BREAK -> _shortBreakDuration.value
                    TimerMode.LONG_BREAK -> _longBreakDuration.value
                }
                _timerState.value = _timerState.value.copy(
                    remainingSeconds = duration * 60
                )
            }
        }
    }
    
    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
