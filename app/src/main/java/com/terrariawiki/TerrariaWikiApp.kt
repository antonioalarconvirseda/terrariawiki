package com.terrariawiki

import android.app.Application
import com.terrariawiki.core.di.networkModule
import com.terrariawiki.features.items.di.itemsModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class TerrariaWikiApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger(Level.INFO)
            androidContext(this@TerrariaWikiApp)
            modules(
                networkModule,
                itemsModule
            )
        }
    }
}
