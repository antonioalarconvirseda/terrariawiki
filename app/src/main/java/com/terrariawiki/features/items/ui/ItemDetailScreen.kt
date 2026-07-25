package com.terrariawiki.features.items.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.terrariawiki.features.items.domain.Item
import com.terrariawiki.features.items.ui.components.ErrorState
import com.terrariawiki.features.items.ui.components.ItemThumbnail
import com.terrariawiki.features.items.ui.components.ItemTypeChips
import com.terrariawiki.features.items.ui.components.LoadingState
import com.terrariawiki.features.items.ui.components.RarityChip
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemDetailScreen(
    name: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ItemDetailViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(name) {
        viewModel.load(name)
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Detalle",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            when (val state = uiState) {
                is ItemDetailViewModel.UiState.Loading -> LoadingState()
                is ItemDetailViewModel.UiState.Error -> ErrorState(
                    message = state.message,
                    onRetry = { viewModel.load(name) }
                )
                is ItemDetailViewModel.UiState.Ready -> ItemDetailContent(item = state.item)
            }
        }
    }
}

@Composable
private fun ItemDetailContent(item: Item) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        DetailHeader(item = item)

        if (!item.tooltip.isNullOrBlank()) {
            DetailSection(title = "Descripción") {
                Text(
                    text = item.tooltip,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        if (item.hasStats) {
            DetailSection(title = "Estadísticas") {
                StatRow(label = "Daño", value = item.damage?.toString())
                StatRow(label = "Defensa", value = item.defense?.toString())
                StatRow(
                    label = "Retroceso",
                    value = item.knockback?.let { "%.2f".format(it) }
                )
                StatRow(label = "Velocidad de uso", value = item.useTime?.toString())
                if (item.isWeapon) {
                    StatRow(
                        label = "Probabilidad de crítico",
                        value = item.critical?.let { "$it%" }
                    )
                    StatRow(
                        label = "Velocidad de proyectil",
                        value = item.velocity?.let { "%.1f".format(it) }
                    )
                }
                if (item.autoSwing) {
                    StatRow(label = "Ataque automático", value = "Sí")
                }
            }
        }

        val inventoryRows = listOfNotNull(
            item.stack?.takeIf { it.isNotBlank() && it != "1" }
                ?.let { "Hasta $it" }
                ?.let { "Apilable" to it }
        )
        if (inventoryRows.isNotEmpty()) {
            DetailSection(title = "Inventario") {
                inventoryRows.forEach { (l, v) -> StatRow(l, v) }
            }
        }

        if (item.listCategories.isNotEmpty()) {
            DetailSection(title = "Categorías") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    item.listCategories.take(4).forEach { cat ->
                        CategoryChip(text = cat)
                    }
                }
            }
        }

        val priceRows = listOfNotNull(
            item.buyRaw?.let { "Precio de compra" to it },
            item.sellRaw?.let { "Precio de venta" to it }
        )
        if (priceRows.isNotEmpty()) {
            DetailSection(title = "Economía") {
                priceRows.forEach { (l, v) -> StatRow(l, v) }
            }
        }

        val infoRows = listOfNotNull(
            item.internalName?.let { "Nombre en el juego" to it },
            item.wikiId?.let { "ID de item" to "#$it" }
        )
        if (infoRows.isNotEmpty()) {
            DetailSection(title = "Información") {
                infoRows.forEach { (l, v) -> InfoRow(l, v) }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun DetailHeader(item: Item) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ItemThumbnail(item = item, size = 128.dp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = item.name,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            RarityChip(rarity = item.rarity, large = true)
            if (item.types.isNotEmpty()) {
                ItemTypeChips(types = item.types)
            }
            if (item.hardmode) {
                HardmodeBadge()
            }
        }
    }
}

@Composable
private fun HardmodeBadge() {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFFFF8A3D))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = "Modo difícil",
            style = MaterialTheme.typography.labelSmall,
            color = Color.White,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun CategoryChip(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.20f))
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.45f),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun DetailSection(
    title: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
            content()
        }
    }
}

@Composable
private fun StatRow(label: String, value: String?) {
    if (value == null) return
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            fontFamily = FontFamily.Monospace
        )
    }
}
