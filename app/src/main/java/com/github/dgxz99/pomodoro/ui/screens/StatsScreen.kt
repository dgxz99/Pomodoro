package com.github.dgxz99.pomodoro.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.github.dgxz99.pomodoro.ui.theme.TextPrimary
import com.github.dgxz99.pomodoro.ui.theme.WarmWhite

@Composable
fun StatsScreen(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(WarmWhite),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "统计 (Coming Soon)",
            fontSize = 24.sp,
            fontWeight = FontWeight.Medium,
            color = TextPrimary
        )
    }
}
