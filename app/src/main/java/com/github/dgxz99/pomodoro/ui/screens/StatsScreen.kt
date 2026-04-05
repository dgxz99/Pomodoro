package com.github.dgxz99.pomodoro.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.dgxz99.pomodoro.ui.components.StatCard
import com.github.dgxz99.pomodoro.ui.theme.TextPrimary
import com.github.dgxz99.pomodoro.ui.theme.WarmWhite
import com.github.dgxz99.pomodoro.viewmodel.StatsViewModel

@Composable
fun StatsScreen(
    viewModel: StatsViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val todayCount by viewModel.todayCount.collectAsState()
    val weekCount by viewModel.weekCount.collectAsState()
    val monthCount by viewModel.monthCount.collectAsState()
    val totalCount by viewModel.totalCount.collectAsState()
    val streakDays by viewModel.streakDays.collectAsState()
    val todayMinutes by viewModel.todayMinutes.collectAsState()
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(WarmWhite)
            .padding(24.dp)
    ) {
        Text(
            text = "统计",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(0.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            item {
                StatCard(
                    title = "今日番茄",
                    value = todayCount,
                    unit = "个"
                )
            }
            
            item {
                StatCard(
                    title = "今日专注",
                    value = todayMinutes,
                    unit = "分钟"
                )
            }
            
            item {
                StatCard(
                    title = "本周番茄",
                    value = weekCount,
                    unit = "个"
                )
            }
            
            item {
                StatCard(
                    title = "本月番茄",
                    value = monthCount,
                    unit = "个"
                )
            }
            
            item {
                StatCard(
                    title = "总计番茄",
                    value = totalCount,
                    unit = "个"
                )
            }
            
            item {
                StatCard(
                    title = "连续使用",
                    value = streakDays,
                    unit = "天"
                )
            }
        }
    }
}
