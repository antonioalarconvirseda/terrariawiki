package com.terrariawiki.features.items.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.terrariawiki.core.ui.theme.rarityColor

@Composable
fun RarityChip(
    rarity: Int,
    modifier: Modifier = Modifier
) {
    val color = rarityColor(rarity)
    Text(
        text = rarityLabel(rarity),
        style = MaterialTheme.typography.labelSmall,
        color = Color.White,
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(color.copy(alpha = 0.85f))
            .padding(horizontal = 8.dp, vertical = 2.dp)
    )
}

private fun rarityLabel(level: Int): String = when (level) {
    -1 -> "Master"
    0 -> "Común"
    1 -> "Azul"
    2 -> "Verde"
    3 -> "Amarillo"
    4 -> "Naranja"
    5 -> "Rojo claro"
    6 -> "Rosa"
    7 -> "Lila"
    8 -> "Violeta"
    9 -> "Ambiguo"
    10 -> "Raro"
    11 -> "Experto"
    else -> "Nivel $level"
}
