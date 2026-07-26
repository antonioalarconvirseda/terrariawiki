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
import com.terrariawiki.core.ui.theme.InventorySlotBorderWidth
import com.terrariawiki.features.items.domain.RarityTier

@Composable
fun RarityChip(
    rarity: Int,
    large: Boolean = false,
    modifier: Modifier = Modifier
) {
    val tier = RarityTier.fromLevel(rarity)
    val shape = RoundedCornerShape(if (large) 12.dp else 8.dp)
    Box(
        modifier = modifier
            .clip(shape)
            .background(Color(tier.colorHex))
            .border(
                width = InventorySlotBorderWidth,
                color = Color.Black.copy(alpha = 0.25f),
                shape = shape
            )
            .padding(
                horizontal = if (large) 14.dp else 8.dp,
                vertical = if (large) 5.dp else 2.dp
            )
    ) {
        Text(
            text = tier.label,
            style = if (large) MaterialTheme.typography.labelLarge
            else MaterialTheme.typography.labelSmall,
            color = Color(tier.textColorHex),
            fontWeight = FontWeight.SemiBold
        )
    }
}
