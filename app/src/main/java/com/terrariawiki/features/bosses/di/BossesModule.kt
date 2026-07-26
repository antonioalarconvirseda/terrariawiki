package com.terrariawiki.features.bosses.di

import com.terrariawiki.features.bosses.data.BossesApi
import com.terrariawiki.features.bosses.data.BossesApiImpl
import com.terrariawiki.features.bosses.data.BossesRepository
import com.terrariawiki.features.bosses.data.BossesRepositoryImpl
import com.terrariawiki.features.bosses.domain.GetBossByNameUseCase
import com.terrariawiki.features.bosses.domain.GetBossesUseCase
import com.terrariawiki.features.bosses.ui.BossDetailViewModel
import com.terrariawiki.features.bosses.ui.BossListViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val bossesModule = module {
    single<BossesApi> { BossesApiImpl(get()) }
    single<BossesRepository> { BossesRepositoryImpl(get()) }

    factory { GetBossesUseCase(get()) }
    factory { GetBossByNameUseCase(get()) }

    viewModel { BossListViewModel(get()) }
    viewModel { BossDetailViewModel(get()) }
}
