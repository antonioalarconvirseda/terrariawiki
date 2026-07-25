package com.terrariawiki.core.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val TerrariaLightColors = lightColorScheme(
    primary = SkyTeal,
    onPrimary = CloudWhite,
    primaryContainer = SkyTeal.copy(alpha = 0.12f),
    onPrimaryContainer = CaveDark,
    secondary = JungleGreen,
    onSecondary = CloudWhite,
    secondaryContainer = JungleGreen.copy(alpha = 0.15f),
    onSecondaryContainer = CaveDark,
    tertiary = GoldGem,
    onTertiary = CaveDark,
    tertiaryContainer = GoldGem.copy(alpha = 0.20f),
    onTertiaryContainer = CaveDark,
    error = SlimeRed,
    onError = CloudWhite,
    background = CloudWhite,
    onBackground = CaveDark,
    surface = CloudWhite,
    onSurface = CaveDark,
    surfaceVariant = StoneGray.copy(alpha = 0.10f),
    onSurfaceVariant = StoneGray,
    outline = StoneGray
)

private val TerrariaDarkColors = darkColorScheme(
    primary = SkyTeal,
    onPrimary = CaveDark,
    primaryContainer = SkyTeal.copy(alpha = 0.25f),
    onPrimaryContainer = CloudWhite,
    secondary = JungleGreen,
    onSecondary = CloudWhite,
    secondaryContainer = JungleGreen.copy(alpha = 0.25f),
    onSecondaryContainer = CloudWhite,
    tertiary = GoldGem,
    onTertiary = CaveDark,
    tertiaryContainer = GoldGem.copy(alpha = 0.25f),
    onTertiaryContainer = CaveDark,
    error = SlimeRed,
    onError = CloudWhite,
    background = CaveDark,
    onBackground = CloudWhite,
    surface = CaveDark,
    onSurface = CloudWhite,
    surfaceVariant = CloudWhite.copy(alpha = 0.10f),
    onSurfaceVariant = CloudWhite.copy(alpha = 0.70f),
    outline = CloudWhite.copy(alpha = 0.40f)
)

@Composable
fun TerrariaWikiTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) TerrariaDarkColors else TerrariaLightColors

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = TerrariaTypography,
        content = content
    )
}
