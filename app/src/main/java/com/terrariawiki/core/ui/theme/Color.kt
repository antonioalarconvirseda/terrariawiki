package com.terrariawiki.core.ui.theme

import androidx.compose.ui.graphics.Color

// Paleta base (modo claro) — saturación subida (~+22%, mismo hue) tras feedback de "colores más vivos"
val SkyTeal = Color(0xFF2F9FCC)
val JungleGreen = Color(0xFF2A9022)
val GoldGem = Color(0xFFFFD03F)
val SlimeRed = Color(0xFFFF3535)
val HellOrange = Color(0xFFFF8A3D)
val CaveDark = Color(0xFF1E1E2A)
val CloudWhite = Color(0xFFF4F1E6)
val StoneGray = Color(0xFF6B7280) // neutro para texto secundario/divisores, sin saturar

// Acentos de bioma (categorías, etiquetas, estados) — misma subida de saturación
val Corruption = Color(0xFF6926B9)
val Crystal = Color(0xFF37EAFE)
val Desert = Color(0xFFEDBA56)
val Abyss = Color(0xFF0E3969)

// Paleta "Cielo Nocturno" (modo oscuro) — reemplaza la anterior "Underworld" (naranja/marrón, no
// gustó); comparte acentos (SkyTeal/JungleGreen/GoldGem/SlimeRed) con el modo claro para marca
// consistente, solo aporta los neutros de fondo/superficie en azul índigo.
val NightBackground = Color(0xFF131A33)
val NightSurface = Color(0xFF1C2444)
val NightSurfaceAlt = Color(0xFF262F58)
val NightOutline = Color(0xFF3A4570)
val StarlightWhite = Color(0xFFEDEFFA)

// Textura de GrassTopAccent
val DirtBrown = Color(0xFF7A4A2B)
val GrassHighlight = Color(0xFF3FAE2E)
