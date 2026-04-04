package com.github.dgxz99.pomodoro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.github.dgxz99.pomodoro.ui.navigation.PomodoroNavGraph
import com.github.dgxz99.pomodoro.ui.theme.PomodoroTheme
import com.github.dgxz99.pomodoro.ui.theme.WarmWhite

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PomodoroTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = WarmWhite
                ) {
                    PomodoroNavGraph()
                }
            }
        }
    }
}
