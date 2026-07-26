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

// "Cielo Nocturno": fondo azul índigo + los mismos acentos vivos del modo claro (no una paleta
// propia tipo lava) — reemplaza "Underworld" (naranja/marrón), que no convenció al usuario.
private val TerrariaDarkColors = darkColorScheme(
    primary = SkyTeal,
    onPrimary = StarlightWhite,
    primaryContainer = SkyTeal.copy(alpha = 0.25f),
    onPrimaryContainer = StarlightWhite,
    secondary = JungleGreen,
    onSecondary = StarlightWhite,
    secondaryContainer = JungleGreen.copy(alpha = 0.25f),
    onSecondaryContainer = StarlightWhite,
    tertiary = GoldGem,
    onTertiary = NightBackground,
    tertiaryContainer = GoldGem.copy(alpha = 0.25f),
    onTertiaryContainer = NightBackground,
    error = SlimeRed,
    onError = StarlightWhite,
    background = NightBackground,
    onBackground = StarlightWhite,
    surface = NightSurface,
    onSurface = StarlightWhite,
    surfaceVariant = NightSurfaceAlt,
    onSurfaceVariant = StarlightWhite.copy(alpha = 0.70f),
    outline = NightOutline
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
        shapes = TerrariaShapes,
        content = content
    )
}
