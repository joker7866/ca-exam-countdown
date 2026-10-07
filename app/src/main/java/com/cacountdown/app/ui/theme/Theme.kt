package com.cacountdown.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val Navy = Color(0xFF0D1B33)
val Gold = Color(0xFFF5A623)
val OnTrackGreen = Color(0xFF2E7D32)
val AttentionAmber = Color(0xFFF2A900)
val BehindOrange = Color(0xFFE8681C)
val UrgentRed = Color(0xFFC62828)
val SurfaceLight = Color(0xFFF4F5F8)

private val LightColors = lightColorScheme(
    primary = Navy,
    secondary = Gold,
    background = SurfaceLight,
    surface = Color.White,
)

private val DarkColors = darkColorScheme(
    primary = Gold,
    secondary = Navy,
)

@Composable
fun CaCountdownTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colors = if (darkTheme) DarkColors else LightColors
    MaterialTheme(colorScheme = colors, content = content)
}
