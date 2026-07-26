package com.terrariawiki.features.items.domain

/**
 * Fuente única de verdad para color/etiqueta de las 13 tiers de rareza de Terraria.
 * Antes duplicado entre `Color.kt::rarityColor()` y overrides locales en `RarityChip`.
 * `colorHex`/`textColorHex` como `Long` (no `Color` de Compose) para mantener el
 * dominio libre de dependencias de UI, igual que `ItemCategory.colorHex`.
 */
enum class RarityTier(
    val level: Int,
    val colorHex: Long,
    val textColorHex: Long,
    val label: String
) {
    MASTER(-1, 0xFF1A1A1A, 0xFFFFFFFF, "Master"),
    COMMON(0, 0xFFE5E5E5, 0xFF333333, "Común"),
    BLUE(1, 0xFF1A8FBF, 0xFFFFFFFF, "Azul"),
    GREEN(2, 0xFF3B7C36, 0xFFFFFFFF, "Verde"),
    YELLOW(3, 0xFFF2C94C, 0xFFFFFFFF, "Amarillo"),
    ORANGE(4, 0xFFFF8A3D, 0xFFFFFFFF, "Naranja"),
    LIGHT_RED(5, 0xFFE94B4B, 0xFFFFFFFF, "Rojo claro"),
    PINK(6, 0xFFE94B8F, 0xFFFFFFFF, "Rosa"),
    LILAC(7, 0xFFB14FCF, 0xFFFFFFFF, "Lila"),
    VIOLET(8, 0xFF8A3DCF, 0xFFFFFFFF, "Violeta"),
    AMBIGUOUS(9, 0xFF6B7280, 0xFFFFFFFF, "Ambiguo"),
    RARE(10, 0xFF4A93B0, 0xFFFFFFFF, "Raro"),
    EXPERT(11, 0xFF1ED4D4, 0xFFFFFFFF, "Experto");

    companion object {
        fun fromLevel(level: Int): RarityTier =
            entries.find { it.level == level } ?: COMMON
    }
}
