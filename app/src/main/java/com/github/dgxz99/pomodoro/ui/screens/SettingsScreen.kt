package com.github.dgxz99.pomodoro.ui.screens

import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
    val context = LocalContext.current
    
    val focusDuration by viewModel.focusDuration.collectAsState()
    val shortBreakDuration by viewModel.shortBreakDuration.collectAsState()
    val longBreakDuration by viewModel.longBreakDuration.collectAsState()
    val pomodorosUntilLongBreak by viewModel.pomodorosUntilLongBreak.collectAsState()
    
    // Ringtone states
    val focusCompleteRingtoneUri by viewModel.focusCompleteRingtoneUri.collectAsState()
    val breakCompleteRingtoneUri by viewModel.breakCompleteRingtoneUri.collectAsState()
    val focusCompleteRingtoneName by viewModel.focusCompleteRingtoneName.collectAsState()
    val breakCompleteRingtoneName by viewModel.breakCompleteRingtoneName.collectAsState()
    
    // Ringtone picker launchers
    val focusRingtoneLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            result.data?.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI, Uri::class.java)
        } else {
            @Suppress("DEPRECATION")
            result.data?.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI)
        }
        viewModel.setFocusCompleteRingtone(uri)
    }
    
    val breakRingtoneLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            result.data?.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI, Uri::class.java)
        } else {
            @Suppress("DEPRECATION")
            result.data?.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI)
        }
        viewModel.setBreakCompleteRingtone(uri)
    }
    
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
        
        // Time settings card
        Text(
            text = "时间设置",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = TextSecondary,
            modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
        )
        
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
        
        // Sound settings card
        Text(
            text = "提示音设置",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = TextSecondary,
            modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
        )
        
        SettingsCard {
            RingtoneSettingItem(
                title = "专注完成提示音",
                ringtoneName = focusCompleteRingtoneName,
                onClick = {
                    val intent = android.content.Intent(RingtoneManager.ACTION_RINGTONE_PICKER).apply {
                        putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION)
                        putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "选择专注完成提示音")
                        putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true)
                        putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false)
                        focusCompleteRingtoneUri?.let { uri ->
                            putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, Uri.parse(uri))
                        }
                    }
                    focusRingtoneLauncher.launch(intent)
                }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            RingtoneSettingItem(
                title = "休息完成提示音",
                ringtoneName = breakCompleteRingtoneName,
                onClick = {
                    val intent = android.content.Intent(RingtoneManager.ACTION_RINGTONE_PICKER).apply {
                        putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION)
                        putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "选择休息完成提示音")
                        putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true)
                        putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false)
                        breakCompleteRingtoneUri?.let { uri ->
                            putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, Uri.parse(uri))
                        }
                    }
                    breakRingtoneLauncher.launch(intent)
                }
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

@Composable
private fun RingtoneSettingItem(
    title: String,
    ringtoneName: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = ringtoneName,
                fontSize = 14.sp,
                color = TomatoRed
            )
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = TextSecondary
        )
    }
}
