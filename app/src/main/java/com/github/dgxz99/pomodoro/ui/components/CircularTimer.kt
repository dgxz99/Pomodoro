package com.github.dgxz99.pomodoro.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.dgxz99.pomodoro.domain.model.TimerMode
import com.github.dgxz99.pomodoro.ui.theme.TomatoRed
import com.github.dgxz99.pomodoro.ui.theme.TomatoRedLight
import com.github.dgxz99.pomodoro.ui.theme.TextPrimary
import com.github.dgxz99.pomodoro.ui.theme.TextSecondary

@Composable
fun CircularTimer(
    remainingSeconds: Int,
    totalSeconds: Int,
    mode: TimerMode,
    modifier: Modifier = Modifier,
    size: Dp = 280.dp,
    strokeWidth: Dp = 12.dp
) {
    val progress = if (totalSeconds > 0) remainingSeconds.toFloat() / totalSeconds else 1f
    val sweepAngle = progress * 360f
    
    val modeText = when (mode) {
        TimerMode.FOCUS -> "专注中"
        TimerMode.SHORT_BREAK -> "短休息"
        TimerMode.LONG_BREAK -> "长休息"
    }
    
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.size(size)
    ) {
        // Background circle
        Canvas(modifier = Modifier.size(size)) {
            drawCircle(
                color = TomatoRedLight,
                style = Stroke(width = strokeWidth.toPx())
            )
        }
        
        // Progress arc
        Canvas(modifier = Modifier.size(size)) {
            drawArc(
                color = TomatoRed,
                startAngle = -90f,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = Stroke(
                    width = strokeWidth.toPx(),
                    cap = StrokeCap.Round
                )
            )
        }
        
        // Time and mode text
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = formatTime(remainingSeconds),
                fontSize = 56.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = modeText,
                fontSize = 18.sp,
                color = TextSecondary
            )
        }
    }
}

private fun formatTime(totalSeconds: Int): String {
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%02d:%02d", minutes, seconds)
}
