package com.terrariawiki.core.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.terrariawiki.core.ui.theme.DirtBrown
import com.terrariawiki.core.ui.theme.GrassHighlight
import com.terrariawiki.core.ui.theme.InventorySlotBorderColor
import com.terrariawiki.core.ui.theme.InventorySlotBorderWidth
import com.terrariawiki.core.ui.theme.JungleGreen

/**
 * Card compartida con el look "slot de inventario" de Terraria: esquinas moderadas
 * + borde sólido, en vez de que cada feature reimplemente su propia Card.
 * `topAccent` añade una franja de hierba arriba, imitando el filo tupido de un bloque de tierra.
 */
@Composable
fun InventorySlotCard(
    modifier: Modifier = Modifier,
    shape: Shape = MaterialTheme.shapes.medium,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    topAccent: Boolean = false,
    content: @Composable ColumnScope.() -> Unit
) {
    OutlinedCard(
        modifier = modifier,
        shape = shape,
        colors = CardDefaults.outlinedCardColors(containerColor = containerColor),
        border = BorderStroke(InventorySlotBorderWidth, InventorySlotBorderColor)
    ) {
        if (topAccent) GrassTopAccent()
        content()
    }
}

@Composable
fun InventorySlotCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = MaterialTheme.shapes.medium,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    topAccent: Boolean = false,
    content: @Composable ColumnScope.() -> Unit
) {
    OutlinedCard(
        onClick = onClick,
        modifier = modifier,
        shape = shape,
        colors = CardDefaults.outlinedCardColors(containerColor = containerColor),
        border = BorderStroke(InventorySlotBorderWidth, InventorySlotBorderColor)
    ) {
        if (topAccent) GrassTopAccent()
        content()
    }
}

// Fracción de altura verde por columna — fija (no aleatoria en runtime) para un resultado
// reproducible, imitando el filo tupido/irregular de césped real en vez de un zigzag simétrico.
private val GRASS_COLUMN_HEIGHTS = listOf(
    0.55f, 0.62f, 0.48f, 0.70f, 0.58f, 0.45f, 0.66f, 0.52f,
    0.60f, 0.72f, 0.50f, 0.64f, 0.56f, 0.68f, 0.46f, 0.61f,
    0.75f, 0.53f, 0.65f, 0.49f, 0.63f, 0.57f, 0.71f, 0.54f
)
private val GRASS_HIGHLIGHT_COLUMNS = setOf(1, 5, 9, 13, 17, 21)

@Composable
private fun GrassTopAccent(modifier: Modifier = Modifier) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(16.dp)
    ) {
        val columns = GRASS_COLUMN_HEIGHTS.size
        val columnWidth = size.width / columns
        GRASS_COLUMN_HEIGHTS.forEachIndexed { index, fraction ->
            val x = index * columnWidth
            val greenHeight = size.height * fraction
            drawRect(
                color = JungleGreen,
                topLeft = Offset(x, 0f),
                size = Size(columnWidth + 0.5f, greenHeight)
            )
            drawRect(
                color = DirtBrown,
                topLeft = Offset(x, greenHeight),
                size = Size(columnWidth + 0.5f, size.height - greenHeight)
            )
            if (index in GRASS_HIGHLIGHT_COLUMNS) {
                drawRect(
                    color = GrassHighlight,
                    topLeft = Offset(x + columnWidth * 0.3f, 0f),
                    size = Size(columnWidth * 0.4f, greenHeight * 0.5f)
                )
            }
        }
    }
}
