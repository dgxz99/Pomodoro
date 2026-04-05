package com.github.dgxz99.pomodoro.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.github.dgxz99.pomodoro.data.preferences.SettingsDataStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    
    private val settingsDataStore = SettingsDataStore(application)
    
    private val _focusDuration = MutableStateFlow(SettingsDataStore.DEFAULT_FOCUS_DURATION)
    val focusDuration: StateFlow<Int> = _focusDuration.asStateFlow()
    
    private val _shortBreakDuration = MutableStateFlow(SettingsDataStore.DEFAULT_SHORT_BREAK_DURATION)
    val shortBreakDuration: StateFlow<Int> = _shortBreakDuration.asStateFlow()
    
    private val _longBreakDuration = MutableStateFlow(SettingsDataStore.DEFAULT_LONG_BREAK_DURATION)
    val longBreakDuration: StateFlow<Int> = _longBreakDuration.asStateFlow()
    
    private val _pomodorosUntilLongBreak = MutableStateFlow(SettingsDataStore.DEFAULT_POMODOROS_UNTIL_LONG_BREAK)
    val pomodorosUntilLongBreak: StateFlow<Int> = _pomodorosUntilLongBreak.asStateFlow()
    
    init {
        loadSettings()
    }
    
    private fun loadSettings() {
        viewModelScope.launch {
            _focusDuration.value = settingsDataStore.focusDuration.first()
            _shortBreakDuration.value = settingsDataStore.shortBreakDuration.first()
            _longBreakDuration.value = settingsDataStore.longBreakDuration.first()
            _pomodorosUntilLongBreak.value = settingsDataStore.pomodorosUntilLongBreak.first()
        }
    }
    
    fun setFocusDuration(minutes: Int) {
        val validMinutes = minutes.coerceIn(1, 120)
        viewModelScope.launch {
            settingsDataStore.setFocusDuration(validMinutes)
            _focusDuration.value = validMinutes
        }
    }
    
    fun setShortBreakDuration(minutes: Int) {
        val validMinutes = minutes.coerceIn(1, 20)
        viewModelScope.launch {
            settingsDataStore.setShortBreakDuration(validMinutes)
            _shortBreakDuration.value = validMinutes
        }
    }
    
    fun setLongBreakDuration(minutes: Int) {
        val validMinutes = minutes.coerceIn(1, 60)
        viewModelScope.launch {
            settingsDataStore.setLongBreakDuration(validMinutes)
            _longBreakDuration.value = validMinutes
        }
    }
    
    fun setPomodorosUntilLongBreak(count: Int) {
        val validCount = count.coerceIn(2, 8)
        viewModelScope.launch {
            settingsDataStore.setPomodorosUntilLongBreak(validCount)
            _pomodorosUntilLongBreak.value = validCount
        }
    }
}
