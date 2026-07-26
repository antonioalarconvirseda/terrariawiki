package com.terrariawiki.features.bosses.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import com.terrariawiki.core.ui.theme.InventorySlotBorderColor
import com.terrariawiki.core.ui.theme.InventorySlotBorderWidth

/** Card compartida con el look "slot de inventario" de Terraria — duplicado del homónimo en `items/ui/components`
 * porque las features no pueden importarse entre sí. */
@Composable
fun InventorySlotCard(
    modifier: Modifier = Modifier,
    shape: Shape = MaterialTheme.shapes.medium,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    content: @Composable ColumnScope.() -> Unit
) {
    OutlinedCard(
        modifier = modifier,
        shape = shape,
        colors = CardDefaults.outlinedCardColors(containerColor = containerColor),
        border = BorderStroke(InventorySlotBorderWidth, InventorySlotBorderColor)
    ) {
        content()
    }
}

@Composable
fun InventorySlotCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = MaterialTheme.shapes.medium,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    content: @Composable ColumnScope.() -> Unit
) {
    OutlinedCard(
        onClick = onClick,
        modifier = modifier,
        shape = shape,
        colors = CardDefaults.outlinedCardColors(containerColor = containerColor),
        border = BorderStroke(InventorySlotBorderWidth, InventorySlotBorderColor)
    ) {
        content()
    }
}
