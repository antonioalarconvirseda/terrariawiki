package com.terrariawiki.features.items.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.terrariawiki.core.ui.theme.rarityColor

@Composable
fun RarityChip(
    rarity: Int,
    large: Boolean = false,
    modifier: Modifier = Modifier
) {
    val color = rarityColor(rarity)
    val shape = RoundedCornerShape(if (large) 12.dp else 8.dp)
    Box(
        modifier = modifier
            .clip(shape)
            .background(color)
            .border(
                width = 1.dp,
                color = Color.Black.copy(alpha = 0.15f),
                shape = shape
            )
            .padding(
                horizontal = if (large) 14.dp else 8.dp,
                vertical = if (large) 5.dp else 2.dp
            )
    ) {
        Text(
            text = rarityLabel(rarity),
            style = if (large) MaterialTheme.typography.labelLarge
            else MaterialTheme.typography.labelSmall,
            color = Color.White,
            fontWeight = FontWeight.SemiBold
        )
    }
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
