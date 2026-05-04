package com.tecsup.stockmin.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val LightScheme = lightColorScheme(
    primary = Color(0xFF1F6B3A),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFB6F0C2),
    secondary = Color(0xFFB45309),
    tertiary = Color(0xFF1E3A8A),
    error = Color(0xFFB3261E),
    background = Color(0xFFF7F9F6),
    surface = Color(0xFFFFFFFF)
)

private val DarkScheme = darkColorScheme(
    primary = Color(0xFF7BD49B),
    onPrimary = Color(0xFF003918),
    primaryContainer = Color(0xFF195030),
    secondary = Color(0xFFFFB870),
    tertiary = Color(0xFF93B4FF),
    error = Color(0xFFF2B8B5),
    background = Color(0xFF101411),
    surface = Color(0xFF161A17)
)

@Composable
fun StockMinTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val colors = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ->
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        darkTheme -> DarkScheme
        else -> LightScheme
    }

    MaterialTheme(
        colorScheme = colors,
        typography = MaterialTheme.typography,
        content = content
    )
}
