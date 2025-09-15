package com.example.phonepe.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = Primary600,
    onPrimary = White,
    secondary = Gray500,
    onSecondary = White,
    tertiary = Primary100,
    background = DarkBg,
    surface = DarkSurface,
    onBackground = DarkTextPrimary,
    onSurface = DarkTextPrimary
)

private val LightColorScheme = lightColorScheme(
    primary = Primary600,
    onPrimary = White,
    secondary = Gray700,
    onSecondary = White,
    tertiary = Primary100,
    background = BackgroundLavender, // Light theme background
    surface = White,
    onBackground = Gray900,
    onSurface = Gray900,
    outline = Gray300
)

@Composable
fun PhonepeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
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

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // Allow the app to draw behind the system bars (edge-to-edge)
            WindowCompat.setDecorFitsSystemWindows(window, false)

            // The actual status bar color should now be primarily controlled by your XML theme.
            // If the XML theme makes it transparent, your Compose content can draw a background.
            // If the XML theme sets a color (e.g., black), that will be used.
            
            // Set status bar icons to light (assuming the status bar background will be dark)
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}