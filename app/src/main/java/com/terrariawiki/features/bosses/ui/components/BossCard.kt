package com.terrariawiki.features.bosses.ui.components

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.terrariawiki.R
import com.terrariawiki.core.network.buildItemImageUrl
import com.terrariawiki.core.ui.theme.InventorySlotBorderColor
import com.terrariawiki.core.ui.theme.InventorySlotBorderWidth
import com.terrariawiki.features.bosses.domain.Boss

@Composable
fun BossCard(
    boss: Boss,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    InventorySlotCard(
        onClick = onClick,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BossThumbnail(boss)
            Spacer(modifier = Modifier.width(12.dp))
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = boss.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (boss.life != null) {
                    Text(
                        text = "Vida: ${boss.life}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
fun BossThumbnail(
    boss: Boss,
    size: Dp = 56.dp,
    modifier: Modifier = Modifier
) {
    val imageModel = boss.imageFilename?.let { buildItemImageUrl(it) }
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
                contentDescription = boss.name,
                modifier = Modifier.size(size),
                placeholder = androidx.compose.ui.res.painterResource(R.drawable.ic_item_placeholder),
                error = androidx.compose.ui.res.painterResource(R.drawable.ic_item_error)
            )
        } else {
            androidx.compose.foundation.Image(
                painter = androidx.compose.ui.res.painterResource(R.drawable.ic_item_placeholder),
                contentDescription = boss.name,
                modifier = Modifier.size(size)
            )
        }
    }
}
