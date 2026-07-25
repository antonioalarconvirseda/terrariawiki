package com.terrariawiki.core.ui.theme

import androidx.compose.ui.graphics.Color

val SkyTeal = Color(0xFF4A93B0)
val JungleGreen = Color(0xFF3B7C36)
val GoldGem = Color(0xFFF2C94C)
val SlimeRed = Color(0xFFE94B4B)
val HellOrange = Color(0xFFFF8A3D)
val CaveDark = Color(0xFF1E1E2A)
val CloudWhite = Color(0xFFF4F1E6)
val StoneGray = Color(0xFF6B7280)

fun rarityColor(level: Int): Color = when (level) {
    -1, 0 -> Color(0xFFFFFFFF)
    1 -> Color(0xFF1A8FBF)
    2 -> Color(0xFF3B7C36)
    3 -> Color(0xFFF2C94C)
    4 -> Color(0xFFFF8A3D)
    5 -> Color(0xFFE94B4B)
    6 -> Color(0xFFE94B8F)
    7 -> Color(0xFFB14FCF)
    8 -> Color(0xFF8A3DCF)
    9 -> Color(0xFF6B7280)
    10 -> Color(0xFF4A93B0)
    11 -> Color(0xFF1ED4D4)
    else -> Color(0xFF6B7280)
}
