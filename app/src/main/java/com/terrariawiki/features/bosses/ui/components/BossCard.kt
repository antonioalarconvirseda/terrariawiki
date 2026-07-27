package com.terrariawiki.features.bosses.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.terrariawiki.core.ui.components.InventorySlotCard
import com.terrariawiki.core.ui.components.WikiThumbnail
import com.terrariawiki.core.ui.theme.Spacing
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
                .padding(Spacing.md),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BossThumbnail(boss)
            Spacer(modifier = Modifier.width(Spacing.md))
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
    WikiThumbnail(
        imageFilename = boss.imageFilename,
        contentDescription = boss.name,
        modifier = modifier,
        size = size
    )
}
