package com.demo.vchat.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color


// Light Theme Colors
val Blue = Color(0xFF002DE3)
val LightBlue = Color(0xFFF1F4FF)
val LightGrey = Color(0xFFD9D9D9)
val OffWhite = Color(0xFFF5F5F5)
val ChatBg = Color(0xFFF7F7FC)
val White = Color(0xFFFFFFFF)
val Black = Color(0xFF000000)

// Dark Theme Colors
val DarkBlue = Color(0xFF3D5AFE)
val DarkLightBlue = Color(0xFF1A1F35)
val DarkGrey = Color(0xFF3C3C3C)
val DarkOffWhite = Color(0xFF1E1E1E)
val DarkChatBg = Color(0xFF121212)
val DarkSurface = Color(0xFF1E1E1E)
val DarkOnSurface = Color(0xFFE1E1E1)

private val LightColorScheme = lightColorScheme(
    primary = Blue,
    secondary = LightBlue,
    tertiary = LightGrey,
    background = ChatBg,
    surface = White,
    onPrimary = White,
    onSecondary = Black,
    onTertiary = Black,
    onBackground = Black,
    onSurface = Black
)

private val DarkColorScheme = darkColorScheme(
    primary = DarkBlue,
    secondary = DarkLightBlue,
    tertiary = DarkGrey,
    background = DarkChatBg,
    surface = DarkSurface,
    onPrimary = Black,
    onSecondary = White,
    onTertiary = White,
    onBackground = DarkOnSurface,
    onSurface = DarkOnSurface
)

@Composable
fun VChatTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}