package com.terrariawiki.features.bosses.domain

data class Boss(
    val name: String,
    val types: List<String>,
    val imageFilename: String?,
    val life: String?,
    val defense: String?,
    val damage: String?,
    val knockback: String?,
    val bannerName: String?
) {
    val hasStats: Boolean
        get() = life != null || defense != null || damage != null || knockback != null
}
