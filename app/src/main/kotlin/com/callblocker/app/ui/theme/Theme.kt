package com.callblocker.app.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary         = Color(0xFF4FC3F7),
    secondary       = Color(0xFF81C784),
    error           = Color(0xFFEF5350),
    background      = Color(0xFF121212),
    surface         = Color(0xFF1E1E1E),
    onPrimary       = Color.Black,
    onSecondary     = Color.Black,
    onBackground    = Color.White,
    onSurface       = Color.White,
)

private val LightColorScheme = lightColorScheme(
    primary         = Color(0xFF0277BD),
    secondary       = Color(0xFF388E3C),
    error           = Color(0xFFD32F2F),
    background      = Color(0xFFF5F5F5),
    surface         = Color.White,
    onPrimary       = Color.White,
    onSecondary     = Color.White,
    onBackground    = Color(0xFF212121),
    onSurface       = Color(0xFF212121),
)

@Composable
fun CallBlockerTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        content = content
    )
}
