package com.github.dgxz99.pomodoro.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.dgxz99.pomodoro.data.preferences.SettingsDataStore
import com.github.dgxz99.pomodoro.domain.model.TimerMode
import com.github.dgxz99.pomodoro.ui.components.CircularTimer
import com.github.dgxz99.pomodoro.ui.components.ControlButtons
import com.github.dgxz99.pomodoro.ui.components.ProgressIndicator
import com.github.dgxz99.pomodoro.ui.theme.TextPrimary
import com.github.dgxz99.pomodoro.ui.theme.TextSecondary
import com.github.dgxz99.pomodoro.ui.theme.TomatoRed
import com.github.dgxz99.pomodoro.ui.theme.WarmWhite
import com.github.dgxz99.pomodoro.ui.theme.White
import com.github.dgxz99.pomodoro.viewmodel.TimerViewModel

@Composable
fun TimerScreen(
    viewModel: TimerViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val timerState by viewModel.timerState.collectAsState()
    val todayCount by viewModel.todayCount.collectAsState()
    val todayMinutes by viewModel.todayMinutes.collectAsState()
    
    val modeTitle = when (timerState.mode) {
        TimerMode.FOCUS -> "专注时间"
        TimerMode.SHORT_BREAK -> "短休息"
        TimerMode.LONG_BREAK -> "长休息"
    }
    
    // Calculate total seconds based on mode
    val totalSeconds = when (timerState.mode) {
        TimerMode.FOCUS -> SettingsDataStore.DEFAULT_FOCUS_DURATION * 60
        TimerMode.SHORT_BREAK -> SettingsDataStore.DEFAULT_SHORT_BREAK_DURATION * 60
        TimerMode.LONG_BREAK -> SettingsDataStore.DEFAULT_LONG_BREAK_DURATION * 60
    }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(WarmWhite)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top section: Mode title
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = modeTitle,
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Progress indicator (番茄数进度)
            ProgressIndicator(
                currentIndex = timerState.currentPomodoroIndex,
                total = SettingsDataStore.DEFAULT_POMODOROS_UNTIL_LONG_BREAK
            )
        }
        
        // Middle section: Circular Timer
        CircularTimer(
            remainingSeconds = timerState.remainingSeconds,
            totalSeconds = totalSeconds,
            mode = timerState.mode
        )
        
        // Bottom section: Controls and stats
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Control buttons
            ControlButtons(
                isRunning = timerState.isRunning,
                onStart = { viewModel.start() },
                onPause = { viewModel.pause() },
                onStop = { viewModel.stop() }
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Today's stats card
            TodayStatsCard(
                pomodoroCount = todayCount,
                focusMinutes = todayMinutes
            )
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun TodayStatsCard(
    pomodoroCount: Int,
    focusMinutes: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatItem(
                value = pomodoroCount.toString(),
                label = "今日番茄"
            )
            
            Spacer(modifier = Modifier.width(24.dp))
            
            StatItem(
                value = "$focusMinutes",
                label = "专注分钟"
            )
        }
    }
}

@Composable
private fun StatItem(
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = TomatoRed
        )
        Text(
            text = label,
            fontSize = 14.sp,
            color = TextSecondary
        )
    }
}
