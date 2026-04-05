package com.github.dgxz99.pomodoro.util

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.github.dgxz99.pomodoro.MainActivity
import com.github.dgxz99.pomodoro.R
import com.github.dgxz99.pomodoro.domain.model.TimerMode

object NotificationHelper {
    
    const val CHANNEL_ID_TIMER_COMPLETE = "timer_complete_v2"
    const val CHANNEL_ID_TIMER_RUNNING = "timer_running"
    const val NOTIFICATION_ID_TIMER_COMPLETE = 1001
    const val NOTIFICATION_ID_TIMER_RUNNING = 1002
    
    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(NotificationManager::class.java)
            
            // Delete old channel if exists (to update sound settings)
            notificationManager.deleteNotificationChannel("timer_complete")
            
            // Timer complete channel (with default sound)
            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
            
            val completeChannel = NotificationChannel(
                CHANNEL_ID_TIMER_COMPLETE,
                "计时器完成",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "计时器完成时的通知"
                enableVibration(true)
                setSound(
                    RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION),
                    audioAttributes
                )
            }
            
            // Timer running channel (silent, ongoing)
            val runningChannel = NotificationChannel(
                CHANNEL_ID_TIMER_RUNNING,
                "计时器运行中",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "计时器运行时的通知"
                enableVibration(false)
                setSound(null, null)
            }
            
            notificationManager.createNotificationChannel(completeChannel)
            notificationManager.createNotificationChannel(runningChannel)
        }
    }
    
    fun showTimerCompleteNotification(
        context: Context,
        mode: TimerMode,
        customRingtoneUri: String? = null
    ) {
        val (title, message) = when (mode) {
            TimerMode.FOCUS -> "专注时间结束" to "休息一下吧！"
            TimerMode.SHORT_BREAK -> "休息结束" to "继续专注！"
            TimerMode.LONG_BREAK -> "长休息结束" to "开始新的番茄周期！"
        }
        
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        // Determine the ringtone to use
        val soundUri = if (customRingtoneUri != null) {
            try {
                Uri.parse(customRingtoneUri)
            } catch (_: Exception) {
                RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            }
        } else {
            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        }
        
        // Check notification permission for Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
        }
        
        // Play sound directly using RingtoneManager for better compatibility
        playNotificationSound(context, soundUri)
        
        // Build notification without sound (we play it separately for custom ringtone support)
        val notification = NotificationCompat.Builder(context, CHANNEL_ID_TIMER_COMPLETE)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setSilent(true)  // We play sound manually
            .build()
        
        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID_TIMER_COMPLETE, notification)
    }
    
    private fun playNotificationSound(context: Context, soundUri: Uri?) {
        try {
            val uri = soundUri ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val ringtone = RingtoneManager.getRingtone(context, uri)
            ringtone?.play()
        } catch (_: Exception) {
            // Fallback: try to play default notification sound
            try {
                val defaultUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                RingtoneManager.getRingtone(context, defaultUri)?.play()
            } catch (_: Exception) {
                // Ignore if we can't play any sound
            }
        }
    }
    
    fun createRunningNotification(
        context: Context,
        mode: TimerMode,
        remainingSeconds: Int
    ): android.app.Notification {
        val modeText = when (mode) {
            TimerMode.FOCUS -> "专注中"
            TimerMode.SHORT_BREAK -> "短休息"
            TimerMode.LONG_BREAK -> "长休息"
        }
        
        val minutes = remainingSeconds / 60
        val seconds = remainingSeconds % 60
        val timeText = String.format("%02d:%02d", minutes, seconds)
        
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        return NotificationCompat.Builder(context, CHANNEL_ID_TIMER_RUNNING)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("番茄钟 - $modeText")
            .setContentText("剩余时间: $timeText")
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .setContentIntent(pendingIntent)
            .setSilent(true)
            .build()
    }
    
    fun cancelRunningNotification(context: Context) {
        NotificationManagerCompat.from(context).cancel(NOTIFICATION_ID_TIMER_RUNNING)
    }
}
