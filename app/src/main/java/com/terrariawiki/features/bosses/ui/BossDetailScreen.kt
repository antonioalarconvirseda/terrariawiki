package com.terrariawiki.features.bosses.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.terrariawiki.features.bosses.domain.Boss
import com.terrariawiki.features.bosses.ui.components.BossThumbnail
import com.terrariawiki.features.bosses.ui.components.ErrorState
import com.terrariawiki.features.bosses.ui.components.InventorySlotCard
import com.terrariawiki.features.bosses.ui.components.LoadingState
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BossDetailScreen(
    name: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: BossDetailViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(name) {
        viewModel.load(name)
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(text = "Detalle", style = MaterialTheme.typography.titleLarge) },
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
                is BossDetailViewModel.UiState.Loading -> LoadingState()
                is BossDetailViewModel.UiState.Error -> ErrorState(
                    message = state.message,
                    onRetry = { viewModel.load(name) }
                )
                is BossDetailViewModel.UiState.Ready -> BossDetailContent(boss = state.boss)
            }
        }
    }
}

@Composable
private fun BossDetailContent(boss: Boss) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        DetailHeader(boss = boss)

        if (boss.hasStats) {
            DetailSection(title = "Estadísticas") {
                StatRow(label = "Vida", value = boss.life)
                StatRow(label = "Defensa", value = boss.defense)
                StatRow(label = "Daño", value = boss.damage)
                StatRow(label = "Retroceso", value = boss.knockback)
            }
        }

        if (!boss.bannerName.isNullOrBlank()) {
            DetailSection(title = "Estandarte") {
                StatRow(label = "Nombre", value = boss.bannerName)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun DetailHeader(boss: Boss) {
    InventorySlotCard(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            BossThumbnail(boss = boss, size = 128.dp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = boss.name,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun DetailSection(
    title: String,
    content: @Composable () -> Unit
) {
    InventorySlotCard(modifier = Modifier.fillMaxWidth()) {
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
