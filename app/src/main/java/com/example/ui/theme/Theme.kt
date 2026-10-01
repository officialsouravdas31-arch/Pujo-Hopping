package com.example.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = BengalRed,
    onPrimary = Color.White,
    primaryContainer = BengalRedDark,
    onPrimaryContainer = FestiveGoldSoft,
    secondary = FestiveGold,
    onSecondary = Color.White,
    secondaryContainer = FestiveGoldSoft,
    onSecondaryContainer = Color(0xFF78350F),
    tertiary = BengalCrimson,
    onTertiary = Color.White,
    background = WarmCreamBg,
    onBackground = DeepCharcoal,
    surface = WarmCreamSurface,
    onSurface = DeepCharcoal,
    surfaceVariant = WarmCreamSubtle,
    onSurfaceVariant = CharcoalMuted,
    outline = Color(0xFFE4D7C5)
)

private val DarkColorScheme = darkColorScheme(
    primary = FestiveGoldLight,
    onPrimary = Color(0xFF451A03),
    primaryContainer = BengalRed,
    onPrimaryContainer = FestiveGoldSoft,
    secondary = FestiveGoldLight,
    onSecondary = Color(0xFF451A03),
    secondaryContainer = Color(0xFF78350F),
    onSecondaryContainer = FestiveGoldSoft,
    tertiary = Color(0xFFF43F5E),
    background = Color(0xFF121214),
    onBackground = Color(0xFFF4F4F5),
    surface = Color(0xFF1C1917),
    onSurface = Color(0xFFF4F4F5),
    surfaceVariant = Color(0xFF292524),
    onSurfaceVariant = Color(0xFFD6D3D1),
    outline = Color(0xFF44403C)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
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
        typography = Typography,
        content = content
    )
}
