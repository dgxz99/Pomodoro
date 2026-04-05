package com.github.dgxz99.pomodoro.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import com.github.dgxz99.pomodoro.domain.model.TimerMode
import com.github.dgxz99.pomodoro.util.NotificationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TimerService : Service() {
    
    private val binder = TimerBinder()
    private var timerJob: Job? = null
    private val serviceScope = CoroutineScope(Dispatchers.Main + Job())
    
    private var remainingSeconds: Int = 0
    private var currentMode: TimerMode = TimerMode.FOCUS
    private var isRunning: Boolean = false
    
    var onTick: ((Int) -> Unit)? = null
    var onComplete: (() -> Unit)? = null
    
    inner class TimerBinder : Binder() {
        fun getService(): TimerService = this@TimerService
    }
    
    override fun onCreate() {
        super.onCreate()
        NotificationHelper.createNotificationChannels(this)
    }
    
    override fun onBind(intent: Intent?): IBinder {
        return binder
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                val seconds = intent.getIntExtra(EXTRA_SECONDS, 25 * 60)
                val mode = intent.getStringExtra(EXTRA_MODE)?.let { 
                    TimerMode.valueOf(it) 
                } ?: TimerMode.FOCUS
                startTimer(seconds, mode)
            }
            ACTION_PAUSE -> pauseTimer()
            ACTION_STOP -> stopTimer()
        }
        return START_NOT_STICKY
    }
    
    fun startTimer(seconds: Int, mode: TimerMode) {
        if (isRunning) return
        
        remainingSeconds = seconds
        currentMode = mode
        isRunning = true
        
        // Start as foreground service
        val notification = NotificationHelper.createRunningNotification(
            this, currentMode, remainingSeconds
        )
        startForeground(NotificationHelper.NOTIFICATION_ID_TIMER_RUNNING, notification)
        
        timerJob = serviceScope.launch {
            while (remainingSeconds > 0 && isRunning) {
                delay(1000)
                remainingSeconds--
                
                // Update notification
                val notification = NotificationHelper.createRunningNotification(
                    this@TimerService, currentMode, remainingSeconds
                )
                startForeground(NotificationHelper.NOTIFICATION_ID_TIMER_RUNNING, notification)
                
                onTick?.invoke(remainingSeconds)
            }
            
            if (remainingSeconds == 0) {
                // Timer completed
                NotificationHelper.showTimerCompleteNotification(this@TimerService, currentMode)
                onComplete?.invoke()
                stopSelf()
            }
        }
    }
    
    fun pauseTimer() {
        timerJob?.cancel()
        isRunning = false
        stopForeground(STOP_FOREGROUND_REMOVE)
    }
    
    fun stopTimer() {
        timerJob?.cancel()
        isRunning = false
        remainingSeconds = 0
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }
    
    fun getRemainingSeconds(): Int = remainingSeconds
    fun isTimerRunning(): Boolean = isRunning
    
    override fun onDestroy() {
        super.onDestroy()
        timerJob?.cancel()
    }
    
    companion object {
        const val ACTION_START = "com.github.dgxz99.pomodoro.ACTION_START"
        const val ACTION_PAUSE = "com.github.dgxz99.pomodoro.ACTION_PAUSE"
        const val ACTION_STOP = "com.github.dgxz99.pomodoro.ACTION_STOP"
        const val EXTRA_SECONDS = "extra_seconds"
        const val EXTRA_MODE = "extra_mode"
    }
}
