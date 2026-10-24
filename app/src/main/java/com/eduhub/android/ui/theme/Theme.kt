package com.eduhub.android.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = EduCyan400,
    onPrimary = EduSlate950,
    primaryContainer = EduCyan600,
    onPrimaryContainer = EduSlate50,
    secondary = EduSky400,
    onSecondary = EduSlate950,
    background = EduSlate950,
    onBackground = EduSlate50,
    surface = EduSlate900,
    onSurface = EduSlate50,
    surfaceVariant = EduSlate800,
    onSurfaceVariant = EduSlate200,
    outline = EduSlate700
)

private val LightColorScheme = lightColorScheme(
    primary = EduSky500,
    onPrimary = EduSlate50,
    primaryContainer = EduCyan400,
    onPrimaryContainer = EduSlate900,
    secondary = EduCyan600,
    onSecondary = EduSlate50,
    background = EduSlate50,
    onBackground = EduSlate900,
    surface = EduSlate50,
    onSurface = EduSlate900,
    surfaceVariant = EduSlate200,
    onSurfaceVariant = EduSlate800,
    outline = EduSlate400
)

@Composable
fun EduHubTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window ?: return@SideEffect
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
