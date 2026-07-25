package com.terrariawiki.features.items.di

import com.terrariawiki.features.items.data.ItemsRepository
import com.terrariawiki.features.items.data.ItemsRepositoryImpl
import com.terrariawiki.features.items.domain.GetItemByNameUseCase
import com.terrariawiki.features.items.domain.GetItemsUseCase
import com.terrariawiki.features.items.domain.ItemCategory
import com.terrariawiki.features.items.domain.SearchItemsUseCase
import com.terrariawiki.features.items.ui.CategoryViewModel
import com.terrariawiki.features.items.ui.ItemDetailViewModel
import com.terrariawiki.features.items.ui.ItemsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val itemsModule = module {
    single<ItemsRepository> { ItemsRepositoryImpl(get()) }

    factory { GetItemsUseCase(get()) }
    factory { SearchItemsUseCase(get()) }
    factory { GetItemByNameUseCase(get()) }

    viewModel { ItemsViewModel(get(), get()) }
    viewModel { ItemDetailViewModel(get(), get()) }
    viewModel { params -> CategoryViewModel(params.get<ItemCategory>(), get()) }
}
