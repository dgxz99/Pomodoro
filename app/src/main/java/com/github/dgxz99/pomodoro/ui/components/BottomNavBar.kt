package com.github.dgxz99.pomodoro.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.github.dgxz99.pomodoro.ui.theme.TomatoRed
import com.github.dgxz99.pomodoro.ui.theme.TextSecondary
import com.github.dgxz99.pomodoro.ui.theme.White

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    data object Timer : BottomNavItem(
        route = "timer",
        title = "计时",
        icon = Icons.Default.Timer
    )
    
    data object Stats : BottomNavItem(
        route = "stats",
        title = "统计",
        icon = Icons.Outlined.BarChart
    )
    
    data object Settings : BottomNavItem(
        route = "settings",
        title = "设置",
        icon = Icons.Default.Settings
    )
}

@Composable
fun BottomNavBar(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        BottomNavItem.Timer,
        BottomNavItem.Stats,
        BottomNavItem.Settings
    )
    
    NavigationBar(
        modifier = modifier,
        containerColor = White
    ) {
        items.forEach { item ->
            val isSelected = currentRoute == item.route
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title
                    )
                },
                label = { Text(item.title) },
                selected = isSelected,
                onClick = { onNavigate(item.route) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = TomatoRed,
                    selectedTextColor = TomatoRed,
                    unselectedIconColor = TextSecondary,
                    unselectedTextColor = TextSecondary,
                    indicatorColor = White
                )
            )
        }
    }
}
