package com.terrariawiki.features.items.domain

enum class ItemCategory(
    val displayName: String,
    val apiFilter: String,
    val iconAsset: String,
    val colorHex: Long,
    val representativeImageFile: String
) {
    WEAPONS("Armas", "weapon", "gavel", 0xFFE94B4B, "Terra Blade.png"),
    ARMOR("Armaduras", "armor", "shield", 0xFF4A93B0, "Molten Helmet.png"),
    ACCESSORIES("Accesorios", "accessory", "watch", 0xFF4DD8E8, "Hermes Boots.png"), // Crystal
    POTIONS("Pociones", "potion", "local_drink", 0xFFF2C94C, "Healing Potion.png"),
    BLOCKS("Bloques", "block", "square_foot", 0xFFD8B36B, "Wood.png"), // Desert
    CONSUMABLES("Consumibles", "consumable", "restaurant", 0xFFFF8A3D, "Apple.png"),
    MECHANISMS("Mecanismos", "mechanism", "settings", 0xFF1B3A5C, "Wire.png"), // Abyss
    FURNITURE("Mobiliario", "furniture", "chair", 0xFFB1824F, "Wooden Chair.png"),
    VANITY("Estética", "vanity", "face", 0xFF6B3FA0, "Top Hat.png"), // Corruption
    MISC("Misceláneo", "furniture", "category", 0xFF1E1E2A, "Bomb.png");

    companion object {
        fun fromOrdinalSafe(ordinal: Int): ItemCategory =
            entries.getOrNull(ordinal) ?: MISC
    }
}
