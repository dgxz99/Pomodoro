package com.github.dgxz99.pomodoro.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pomodoro_records")
data class PomodoroRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val completedAt: Long,  // Unix timestamp in milliseconds
    val duration: Int       // Duration in minutes
)
