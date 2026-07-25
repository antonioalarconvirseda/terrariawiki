package com.terrariawiki.core.di

import com.terrariawiki.core.network.createHttpClient
import com.terrariawiki.features.items.data.ItemsApi
import com.terrariawiki.features.items.data.ItemsApiImpl
import org.koin.dsl.module

val networkModule = module {
    single { createHttpClient() }
    single<ItemsApi> { ItemsApiImpl(get()) }
}
