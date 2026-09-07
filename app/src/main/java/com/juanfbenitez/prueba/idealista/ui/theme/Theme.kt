package com.juanfbenitez.prueba.idealista.ui.theme

import android.app.Activity
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

private val DarkColorScheme = darkColorScheme(
    primary = IdealistaPrimary,
    onPrimary = IdealistaBlack,
    primaryContainer = IdealistaPrimaryDark,
    secondary = IdealistaSecondary,
    onSecondary = Color.White,
    tertiary = IdealistaSecondary,
    background = IdealistaBlack,
    onBackground = Color.White,
    surface = IdealistaGrayDark,
    onSurface = Color.White,
    error = IdealistaRed,
    outline = IdealistaDivider
)

private val LightColorScheme = lightColorScheme(
    primary = IdealistaPrimary,
    onPrimary = IdealistaBlack,
    primaryContainer = IdealistaPrimaryDark,
    secondary = IdealistaSecondary,
    onSecondary = Color.White,
    tertiary = IdealistaSecondary,
    background = Color.White,
    onBackground = IdealistaBlack,
    surface = Color.White,
    onSurface = IdealistaBlack,
    onSurfaceVariant = IdealistaGray,
    error = IdealistaRed,
    outline = IdealistaDivider
)

@Composable
fun PruebaIdealistaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is disabled by default so the idealista brand palette is always used instead
    // of colors derived from the device wallpaper (Android 12+).
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}