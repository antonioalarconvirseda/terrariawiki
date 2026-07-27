package com.terrariawiki.core.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.terrariawiki.R
import com.terrariawiki.core.network.buildItemImageUrl
import com.terrariawiki.core.ui.theme.InventorySlotBorderColor
import com.terrariawiki.core.ui.theme.InventorySlotBorderWidth

/**
 * Miniatura bordeada estilo "slot de inventario" para una imagen de la wiki
 * (item, boss o cualquier entidad con `imageFilename`). Desacoplada de tipos de
 * dominio de feature para que Items/Bosses/futuras features compartan una sola
 * implementación en vez de reimplementar el mismo Box+AsyncImage por feature.
 */
@Composable
fun WikiThumbnail(
    imageFilename: String?,
    contentDescription: String,
    modifier: Modifier = Modifier,
    size: Dp = 56.dp
) {
    val imageModel = imageFilename?.let { buildItemImageUrl(it) }
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
                contentDescription = contentDescription,
                modifier = Modifier.size(size),
                placeholder = painterResource(R.drawable.ic_item_placeholder),
                error = painterResource(R.drawable.ic_item_error)
            )
        } else {
            Image(
                painter = painterResource(R.drawable.ic_item_placeholder),
                contentDescription = contentDescription,
                modifier = Modifier.size(size)
            )
        }
    }
}
