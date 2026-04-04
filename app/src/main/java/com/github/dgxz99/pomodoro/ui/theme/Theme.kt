package com.github.dgxz99.pomodoro.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = TomatoRed,
    onPrimary = White,
    primaryContainer = TomatoRedLight,
    onPrimaryContainer = TextPrimary,
    secondary = TomatoRedLight,
    onSecondary = TextPrimary,
    secondaryContainer = TomatoRedLight,
    onSecondaryContainer = TextPrimary,
    background = WarmWhite,
    onBackground = TextPrimary,
    surface = White,
    onSurface = TextPrimary,
    surfaceVariant = WarmWhite,
    onSurfaceVariant = TextSecondary,
    outline = DividerColor
)

// Keep dark theme simple for now (can be enhanced later)
private val DarkColorScheme = darkColorScheme(
    primary = TomatoRed,
    onPrimary = White,
    primaryContainer = TomatoRedDark,
    onPrimaryContainer = White,
    secondary = TomatoRedLight,
    onSecondary = TextPrimary,
    background = Color(0xFF1A1A1A),
    onBackground = White,
    surface = Color(0xFF2D2D2D),
    onSurface = White
)

@Composable
fun PomodoroTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
