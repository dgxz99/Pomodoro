package com.github.dgxz99.pomodoro.domain.model

data class TimerState(
    val mode: TimerMode = TimerMode.FOCUS,
    val remainingSeconds: Int = 0,  // 0 means not initialized yet
    val isRunning: Boolean = false,
    val currentPomodoroIndex: Int = 0  // 0-based index
)
