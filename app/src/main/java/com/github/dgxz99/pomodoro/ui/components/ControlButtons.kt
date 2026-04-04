package com.github.dgxz99.pomodoro.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.dgxz99.pomodoro.ui.theme.TomatoRed
import com.github.dgxz99.pomodoro.ui.theme.White

@Composable
fun ControlButtons(
    isRunning: Boolean,
    onStart: () -> Unit,
    onPause: () -> Unit,
    onStop: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isRunning) {
            // Pause button
            FloatingActionButton(
                onClick = onPause,
                shape = CircleShape,
                containerColor = White,
                contentColor = TomatoRed,
                elevation = FloatingActionButtonDefaults.elevation(
                    defaultElevation = 4.dp
                ),
                modifier = Modifier.size(72.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Pause,
                    contentDescription = "暂停",
                    modifier = Modifier.size(36.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(24.dp))
            
            // Stop button
            SmallFloatingActionButton(
                onClick = onStop,
                shape = CircleShape,
                containerColor = White,
                contentColor = TomatoRed,
                elevation = FloatingActionButtonDefaults.elevation(
                    defaultElevation = 4.dp
                ),
                modifier = Modifier.size(56.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Stop,
                    contentDescription = "停止",
                    modifier = Modifier.size(28.dp)
                )
            }
        } else {
            // Start button
            FloatingActionButton(
                onClick = onStart,
                shape = CircleShape,
                containerColor = White,
                contentColor = TomatoRed,
                elevation = FloatingActionButtonDefaults.elevation(
                    defaultElevation = 4.dp
                ),
                modifier = Modifier.size(72.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "开始",
                    modifier = Modifier.size(36.dp)
                )
            }
        }
    }
}
