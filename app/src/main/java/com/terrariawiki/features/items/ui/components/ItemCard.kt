package com.terrariawiki.features.items.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.terrariawiki.R
import com.terrariawiki.core.network.buildItemImageUrl
import com.terrariawiki.core.ui.theme.InventorySlotBorderColor
import com.terrariawiki.core.ui.theme.InventorySlotBorderWidth
import com.terrariawiki.features.items.domain.Item

@Composable
fun ItemCard(
    item: Item,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    InventorySlotCard(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ItemThumbnail(item)
            Spacer(modifier = Modifier.width(12.dp))
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (item.types.isNotEmpty()) {
                    Text(
                        text = item.types.joinToString(" · "),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                if (item.tooltip != null && item.tooltip.isNotBlank()) {
                    Text(
                        text = item.tooltip,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            RarityChip(rarity = item.rarity)
        }
    }
}

@Composable
fun ItemThumbnail(
    item: Item,
    size: androidx.compose.ui.unit.Dp = 56.dp,
    modifier: Modifier = Modifier
) {
    val imageModel = item.imageFilename?.let { filename ->
        buildItemImageUrl(filename)
    }
    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .border(InventorySlotBorderWidth, InventorySlotBorderColor, RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center
    ) {
        if (imageModel != null) {
            AsyncImage(
                model = imageModel,
                contentDescription = item.name,
                modifier = Modifier.size(size),
                placeholder = androidx.compose.ui.res.painterResource(R.drawable.ic_item_placeholder),
                error = androidx.compose.ui.res.painterResource(R.drawable.ic_item_error)
            )
        } else {
            androidx.compose.foundation.Image(
                painter = androidx.compose.ui.res.painterResource(R.drawable.ic_item_placeholder),
                contentDescription = item.name,
                modifier = Modifier.size(size)
            )
        }
    }
}

@Composable
fun ItemTypeChips(
    types: List<String>,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        types.take(3).forEach { type ->
            AssistChip(
                onClick = {},
                label = {
                    Text(
                        text = type,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White
                    )
                },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                )
            )
        }
    }
}
