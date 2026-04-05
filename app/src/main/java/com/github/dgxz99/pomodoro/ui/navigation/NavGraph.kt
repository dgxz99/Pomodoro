package com.github.dgxz99.pomodoro.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.github.dgxz99.pomodoro.ui.components.BottomNavBar
import com.github.dgxz99.pomodoro.ui.components.BottomNavItem
import com.github.dgxz99.pomodoro.ui.screens.SettingsScreen
import com.github.dgxz99.pomodoro.ui.screens.StatsScreen
import com.github.dgxz99.pomodoro.ui.screens.TimerScreen
import com.github.dgxz99.pomodoro.viewmodel.StatsViewModel
import com.github.dgxz99.pomodoro.viewmodel.TimerViewModel

@Composable
fun PomodoroNavGraph(
    navController: NavHostController = rememberNavController(),
    timerViewModel: TimerViewModel = viewModel(),
    statsViewModel: StatsViewModel = viewModel()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: BottomNavItem.Timer.route
    
    // Refresh data when navigating between screens
    LaunchedEffect(currentRoute) {
        when (currentRoute) {
            BottomNavItem.Timer.route -> timerViewModel.refreshSettings()
            BottomNavItem.Stats.route -> statsViewModel.refresh()
        }
    }
    
    Scaffold(
        bottomBar = {
            BottomNavBar(
                currentRoute = currentRoute,
                onNavigate = { route ->
                    navController.navigate(route) {
                        // Pop up to the start destination to avoid building up a large stack
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        // Avoid multiple copies of the same destination
                        launchSingleTop = true
                        // Restore state when reselecting a previously selected item
                        restoreState = true
                    }
                }
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.Timer.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(BottomNavItem.Timer.route) {
                TimerScreen(viewModel = timerViewModel)
            }
            composable(BottomNavItem.Stats.route) {
                StatsScreen(viewModel = statsViewModel)
            }
            composable(BottomNavItem.Settings.route) {
                SettingsScreen()
            }
        }
    }
}
