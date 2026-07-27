package com.terrariawiki.features.bosses.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import com.terrariawiki.core.ui.components.DetailSection
import com.terrariawiki.core.ui.components.ErrorState
import com.terrariawiki.core.ui.components.InventorySlotCard
import com.terrariawiki.core.ui.components.LoadingState
import com.terrariawiki.core.ui.components.StatRow
import com.terrariawiki.core.ui.theme.Spacing
import com.terrariawiki.features.bosses.domain.Boss
import com.terrariawiki.features.bosses.ui.components.BossThumbnail
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
                title = {
                    Text(
                        text = (uiState as? BossDetailViewModel.UiState.Ready)?.boss?.name ?: "Detalle",
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
                is BossDetailViewModel.UiState.Loading -> LoadingState(message = "Cargando jefe…")
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
            .padding(Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.lg)
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

        Spacer(modifier = Modifier.height(Spacing.lg))
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
                .padding(Spacing.xl),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Spacing.sm)
        ) {
            BossThumbnail(boss = boss, size = 128.dp)
            Spacer(modifier = Modifier.height(Spacing.sm))
            Text(
                text = boss.name,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
