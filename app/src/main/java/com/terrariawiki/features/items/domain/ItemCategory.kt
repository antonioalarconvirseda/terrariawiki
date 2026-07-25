package com.terrariawiki.features.items.domain

enum class ItemCategory(
    val displayName: String,
    val apiFilter: String,
    val iconAsset: String,
    val colorHex: Long
) {
    WEAPONS("Armas", "weapon", "gavel", 0xFFE94B4B),
    ARMOR("Armaduras", "armor", "shield", 0xFF4A93B0),
    ACCESSORIES("Accesorios", "accessory", "watch", 0xFF3B7C36),
    POTIONS("Pociones", "potion", "local_drink", 0xFFF2C94C),
    BLOCKS("Bloques", "block", "square_foot", 0xFF8B5A2B),
    CONSUMABLES("Consumibles", "consumable", "restaurant", 0xFFFF8A3D),
    MECHANISMS("Mecanismos", "mechanism", "settings", 0xFF6B7280),
    FURNITURE("Mobiliario", "furniture", "chair", 0xFFB1824F),
    VANITY("Estética", "vanity", "face", 0xFFE94B8F),
    MISC("Misceláneo", "furniture", "category", 0xFF1E1E2A);

    companion object {
        fun fromOrdinalSafe(ordinal: Int): ItemCategory =
            entries.getOrNull(ordinal) ?: MISC
    }
}
