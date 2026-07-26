package com.terrariawiki.core.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

val TerrariaShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(10.dp),
    large = RoundedCornerShape(12.dp),
    extraLarge = RoundedCornerShape(12.dp)
)

// Borde "slot de inventario" compartido por cards y contenedores de icono
val InventorySlotBorderWidth = 2.dp

val InventorySlotBorderColor: Color
    @Composable get() = MaterialTheme.colorScheme.outline
