package com.github.dgxz99.pomodoro.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsDataStore(private val context: Context) {
    
    companion object {
        val FOCUS_DURATION = intPreferencesKey("focus_duration")
        val SHORT_BREAK_DURATION = intPreferencesKey("short_break_duration")
        val LONG_BREAK_DURATION = intPreferencesKey("long_break_duration")
        val POMODOROS_UNTIL_LONG_BREAK = intPreferencesKey("pomodoros_until_long_break")
        val FOCUS_COMPLETE_RINGTONE = stringPreferencesKey("focus_complete_ringtone")
        val BREAK_COMPLETE_RINGTONE = stringPreferencesKey("break_complete_ringtone")
        
        // Default values (in minutes)
        const val DEFAULT_FOCUS_DURATION = 25
        const val DEFAULT_SHORT_BREAK_DURATION = 5
        const val DEFAULT_LONG_BREAK_DURATION = 15
        const val DEFAULT_POMODOROS_UNTIL_LONG_BREAK = 4
    }
    
    val focusDuration: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[FOCUS_DURATION] ?: DEFAULT_FOCUS_DURATION
    }
    
    val shortBreakDuration: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[SHORT_BREAK_DURATION] ?: DEFAULT_SHORT_BREAK_DURATION
    }
    
    val longBreakDuration: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[LONG_BREAK_DURATION] ?: DEFAULT_LONG_BREAK_DURATION
    }
    
    val pomodorosUntilLongBreak: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[POMODOROS_UNTIL_LONG_BREAK] ?: DEFAULT_POMODOROS_UNTIL_LONG_BREAK
    }
    
    // Ringtone URIs (null means use system default)
    val focusCompleteRingtone: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[FOCUS_COMPLETE_RINGTONE]
    }
    
    val breakCompleteRingtone: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[BREAK_COMPLETE_RINGTONE]
    }
    
    suspend fun setFocusDuration(minutes: Int) {
        context.dataStore.edit { preferences ->
            preferences[FOCUS_DURATION] = minutes
        }
    }
    
    suspend fun setShortBreakDuration(minutes: Int) {
        context.dataStore.edit { preferences ->
            preferences[SHORT_BREAK_DURATION] = minutes
        }
    }
    
    suspend fun setLongBreakDuration(minutes: Int) {
        context.dataStore.edit { preferences ->
            preferences[LONG_BREAK_DURATION] = minutes
        }
    }
    
    suspend fun setPomodorosUntilLongBreak(count: Int) {
        context.dataStore.edit { preferences ->
            preferences[POMODOROS_UNTIL_LONG_BREAK] = count
        }
    }
    
    suspend fun setFocusCompleteRingtone(uri: String?) {
        context.dataStore.edit { preferences ->
            if (uri != null) {
                preferences[FOCUS_COMPLETE_RINGTONE] = uri
            } else {
                preferences.remove(FOCUS_COMPLETE_RINGTONE)
            }
        }
    }
    
    suspend fun setBreakCompleteRingtone(uri: String?) {
        context.dataStore.edit { preferences ->
            if (uri != null) {
                preferences[BREAK_COMPLETE_RINGTONE] = uri
            } else {
                preferences.remove(BREAK_COMPLETE_RINGTONE)
            }
        }
    }
}
