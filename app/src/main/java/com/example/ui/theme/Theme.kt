package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val KaleidoscopeColorScheme = darkColorScheme(
    primary = StudioAccent,
    onPrimary = StudioBlack,
    secondary = StudioIce,
    onSecondary = StudioBlack,
    tertiary = StudioTextSecondary,
    onTertiary = StudioBlack,
    background = StudioBlack,
    onBackground = StudioTextPrimary,
    surface = StudioSurface,
    onSurface = StudioTextPrimary,
    surfaceVariant = StudioSurfaceElevated,
    onSurfaceVariant = StudioTextSecondary
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = KaleidoscopeColorScheme,
        typography = Typography,
        content = content
    )
}

