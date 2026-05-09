package com.grama.wastetracker.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = GreenPrimary,
    onPrimary = GreenOnPrimary,
    primaryContainer = GreenSurface,
    onPrimaryContainer = GreenPrimaryDark,
    secondary = BluePrimary,
    onSecondary = GreenOnPrimary,
    secondaryContainer = BlueLight,
    onSecondaryContainer = BluePrimaryDark,
    tertiary = OrangeAccent,
    error = RedError,
    background = BackgroundLight,
    onBackground = OnSurfaceLight,
    surface = SurfaceLight,
    onSurface = OnSurfaceLight,
    onSurfaceVariant = OnSurfaceVariantLight,
    outline = OutlineLight,
    surfaceVariant = GreenSurface,
)

private val DarkColorScheme = darkColorScheme(
    primary = GreenPrimaryNight,
    onPrimary = GreenPrimaryDark,
    primaryContainer = GreenPrimaryDark,
    onPrimaryContainer = GreenSurface,
    secondary = BluePrimaryNight,
    onSecondary = BluePrimaryDark,
    secondaryContainer = BluePrimaryDark,
    onSecondaryContainer = BlueLight,
    tertiary = OrangeAccent,
    error = RedError,
    background = BackgroundDark,
    onBackground = OnSurfaceDark,
    surface = SurfaceDark,
    onSurface = OnSurfaceDark,
)

@Composable
fun GramaWasteTrackerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalView.current.context
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = GramaTypography,
        shapes = GramaShapes,
        content = content
    )
}
