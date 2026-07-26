package com.terrariawiki.features.items.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import coil.compose.AsyncImage
import com.terrariawiki.R
import com.terrariawiki.core.network.buildItemImageUrl
import com.terrariawiki.core.ui.theme.Spacing
import com.terrariawiki.features.items.domain.ItemCategory
import com.terrariawiki.features.items.ui.components.InventorySlotCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onCategoryClick: (ItemCategory) -> Unit,
    onSearchClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Terraria Wiki",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                actions = {
                    IconButton(onClick = onSearchClick) {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = "Buscar",
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
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            contentPadding = PaddingValues(Spacing.md),
            verticalArrangement = Arrangement.spacedBy(Spacing.sm),
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) { WelcomeCard() }
            item(span = { GridItemSpan(maxLineSpan) }) { LatestVersionsCard() }
            items(ItemCategory.entries.toList()) { category ->
                CategoryCard(
                    category = category,
                    onClick = { onCategoryClick(category) }
                )
            }
        }
    }
}

@Composable
private fun WelcomeCard() {
    InventorySlotCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(Spacing.md)) {
            Text(
                text = "¡Bienvenido a Terraria Wiki!",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Tu enciclopedia de bolsillo de Terraria, con datos en vivo de la wiki oficial: items, recetas, rareza y mucho más.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = Spacing.xs)
            )
        }
    }
}

@Composable
private fun LatestVersionsCard() {
    // Valores estáticos: la API Cargo que consume la app no expone la versión del juego.
    // Actualizar a mano si Terraria libera una versión nueva.
    val versions = listOf(
        "PC" to "1.4.5.6",
        "Consolas" to "1.4.5",
        "Móvil" to "1.4.5",
        "Switch" to "1.4.5"
    )
    InventorySlotCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(Spacing.md)) {
            Text(
                text = "Últimas versiones",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )
            versions.forEach { (platform, version) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = Spacing.xs),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = platform,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = version,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
private fun CategoryCard(
    category: ItemCategory,
    onClick: () -> Unit
) {
    InventorySlotCard(
        onClick = onClick,
        topAccent = true,
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(Spacing.sm),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AsyncImage(
                model = buildItemImageUrl(category.representativeImageFile),
                contentDescription = category.displayName,
                modifier = Modifier.size(48.dp),
                placeholder = androidx.compose.ui.res.painterResource(R.drawable.ic_item_placeholder),
                error = androidx.compose.ui.res.painterResource(R.drawable.ic_item_error)
            )
            Text(
                text = category.displayName,
                style = MaterialTheme.typography.labelLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(top = Spacing.xs)
            )
        }
    }
}
