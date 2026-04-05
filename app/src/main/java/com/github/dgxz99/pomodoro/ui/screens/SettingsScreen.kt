package com.github.dgxz99.pomodoro.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
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
import com.github.dgxz99.pomodoro.ui.theme.TextPrimary
import com.github.dgxz99.pomodoro.ui.theme.TextSecondary
import com.github.dgxz99.pomodoro.ui.theme.TomatoRed
import com.github.dgxz99.pomodoro.ui.theme.TomatoRedLight
import com.github.dgxz99.pomodoro.ui.theme.WarmWhite
import com.github.dgxz99.pomodoro.ui.theme.White
import com.github.dgxz99.pomodoro.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val focusDuration by viewModel.focusDuration.collectAsState()
    val shortBreakDuration by viewModel.shortBreakDuration.collectAsState()
    val longBreakDuration by viewModel.longBreakDuration.collectAsState()
    val pomodorosUntilLongBreak by viewModel.pomodorosUntilLongBreak.collectAsState()
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(WarmWhite)
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "设置",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        SettingsCard {
            SettingItem(
                title = "专注时间",
                value = focusDuration,
                unit = "分钟",
                min = 1f,
                max = 120f,
                onValueChange = { viewModel.setFocusDuration(it.toInt()) }
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            SettingItem(
                title = "短休息时间",
                value = shortBreakDuration,
                unit = "分钟",
                min = 1f,
                max = 20f,
                onValueChange = { viewModel.setShortBreakDuration(it.toInt()) }
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            SettingItem(
                title = "长休息时间",
                value = longBreakDuration,
                unit = "分钟",
                min = 1f,
                max = 60f,
                onValueChange = { viewModel.setLongBreakDuration(it.toInt()) }
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            SettingItem(
                title = "长休息间隔",
                value = pomodorosUntilLongBreak,
                unit = "个番茄",
                min = 2f,
                max = 8f,
                onValueChange = { viewModel.setPomodorosUntilLongBreak(it.toInt()) }
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Info card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = TomatoRedLight.copy(alpha = 0.5f)
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "提示",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "设置会自动保存并立即生效。",
                    fontSize = 14.sp,
                    color = TextSecondary
                )
            }
        }
    }
}

@Composable
private fun SettingsCard(
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            content()
        }
    }
}

@Composable
private fun SettingItem(
    title: String,
    value: Int,
    unit: String,
    min: Float,
    max: Float,
    onValueChange: (Float) -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = TextPrimary,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "$value $unit",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TomatoRed
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Slider(
            value = value.toFloat(),
            onValueChange = onValueChange,
            valueRange = min..max,
            colors = SliderDefaults.colors(
                thumbColor = TomatoRed,
                activeTrackColor = TomatoRed,
                inactiveTrackColor = TomatoRedLight
            )
        )
    }
}
