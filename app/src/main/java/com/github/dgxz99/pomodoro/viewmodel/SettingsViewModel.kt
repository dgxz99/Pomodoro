package com.github.dgxz99.pomodoro.viewmodel

import android.app.Application
import android.media.RingtoneManager
import android.net.Uri
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
    
    // Track initialization state
    private val _isInitialized = MutableStateFlow(false)
    val isInitialized: StateFlow<Boolean> = _isInitialized.asStateFlow()
    
    private val _focusDuration = MutableStateFlow(SettingsDataStore.DEFAULT_FOCUS_DURATION)
    val focusDuration: StateFlow<Int> = _focusDuration.asStateFlow()
    
    private val _shortBreakDuration = MutableStateFlow(SettingsDataStore.DEFAULT_SHORT_BREAK_DURATION)
    val shortBreakDuration: StateFlow<Int> = _shortBreakDuration.asStateFlow()
    
    private val _longBreakDuration = MutableStateFlow(SettingsDataStore.DEFAULT_LONG_BREAK_DURATION)
    val longBreakDuration: StateFlow<Int> = _longBreakDuration.asStateFlow()
    
    private val _pomodorosUntilLongBreak = MutableStateFlow(SettingsDataStore.DEFAULT_POMODOROS_UNTIL_LONG_BREAK)
    val pomodorosUntilLongBreak: StateFlow<Int> = _pomodorosUntilLongBreak.asStateFlow()
    
    // Ringtone URIs (null means system default)
    private val _focusCompleteRingtoneUri = MutableStateFlow<String?>(null)
    val focusCompleteRingtoneUri: StateFlow<String?> = _focusCompleteRingtoneUri.asStateFlow()
    
    private val _breakCompleteRingtoneUri = MutableStateFlow<String?>(null)
    val breakCompleteRingtoneUri: StateFlow<String?> = _breakCompleteRingtoneUri.asStateFlow()
    
    // Ringtone display names
    private val _focusCompleteRingtoneName = MutableStateFlow("系统默认")
    val focusCompleteRingtoneName: StateFlow<String> = _focusCompleteRingtoneName.asStateFlow()
    
    private val _breakCompleteRingtoneName = MutableStateFlow("系统默认")
    val breakCompleteRingtoneName: StateFlow<String> = _breakCompleteRingtoneName.asStateFlow()
    
    init {
        loadSettings()
    }
    
    private fun loadSettings() {
        viewModelScope.launch {
            _focusDuration.value = settingsDataStore.focusDuration.first()
            _shortBreakDuration.value = settingsDataStore.shortBreakDuration.first()
            _longBreakDuration.value = settingsDataStore.longBreakDuration.first()
            _pomodorosUntilLongBreak.value = settingsDataStore.pomodorosUntilLongBreak.first()
            
            // Load ringtone settings
            val focusRingtoneUri = settingsDataStore.focusCompleteRingtone.first()
            _focusCompleteRingtoneUri.value = focusRingtoneUri
            _focusCompleteRingtoneName.value = getRingtoneName(focusRingtoneUri)
            
            val breakRingtoneUri = settingsDataStore.breakCompleteRingtone.first()
            _breakCompleteRingtoneUri.value = breakRingtoneUri
            _breakCompleteRingtoneName.value = getRingtoneName(breakRingtoneUri)
            
            // Mark as initialized
            _isInitialized.value = true
        }
    }
    
    private fun getRingtoneName(uriString: String?): String {
        if (uriString == null) return "系统默认"
        return try {
            val uri = Uri.parse(uriString)
            val ringtone = RingtoneManager.getRingtone(getApplication(), uri)
            ringtone?.getTitle(getApplication()) ?: "系统默认"
        } catch (_: Exception) {
            "系统默认"
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
    
    fun setFocusCompleteRingtone(uri: Uri?) {
        viewModelScope.launch {
            val uriString = uri?.toString()
            settingsDataStore.setFocusCompleteRingtone(uriString)
            _focusCompleteRingtoneUri.value = uriString
            _focusCompleteRingtoneName.value = getRingtoneName(uriString)
        }
    }
    
    fun setBreakCompleteRingtone(uri: Uri?) {
        viewModelScope.launch {
            val uriString = uri?.toString()
            settingsDataStore.setBreakCompleteRingtone(uriString)
            _breakCompleteRingtoneUri.value = uriString
            _breakCompleteRingtoneName.value = getRingtoneName(uriString)
        }
    }
}
