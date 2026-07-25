package com.terrariawiki.core.network

import android.content.Context
import android.util.Log
import coil.ImageLoader
import coil.disk.DiskCache
import coil.memory.MemoryCache
import coil.request.CachePolicy
import java.util.concurrent.atomic.AtomicLong
import okhttp3.Dispatcher
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response

object CoilConfig {
    const val LOG_TAG = "CoilHttp"
    const val MAX_CONCURRENT_PER_HOST = 10
    const val MAX_TOTAL_REQUESTS = 20
    const val CACHE_BYTES = 50L * 1024 * 1024
    const val TOKEN_PERIOD_MS = 100L
}

class TokenBucketInterceptor(
    private val periodMs: Long = CoilConfig.TOKEN_PERIOD_MS
) : Interceptor {
    private val nextSlot = AtomicLong(0L)

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val now = System.currentTimeMillis()
        var reserved = 0L
        while (true) {
            val current = nextSlot.get()
            val candidate = maxOf(current, now) + periodMs
            if (nextSlot.compareAndSet(current, candidate)) {
                reserved = candidate
                break
            }
        }
        val waitMs = (reserved - now).coerceAtLeast(0L)
        if (waitMs > 0) {
            Thread.sleep(waitMs)
        }
        val response = chain.proceed(request)
        Log.d(CoilConfig.LOG_TAG, "${response.code} ${request.url.toString().take(120)}")
        return response
    }
}

fun createCoilImageLoader(context: Context): ImageLoader {
    val dispatcher = Dispatcher().apply {
        maxRequests = CoilConfig.MAX_TOTAL_REQUESTS
        maxRequestsPerHost = CoilConfig.MAX_CONCURRENT_PER_HOST
    }
    val okHttpClient = OkHttpClient.Builder()
        .dispatcher(dispatcher)
        .addInterceptor(TokenBucketInterceptor())
        .build()
    return ImageLoader.Builder(context)
        .okHttpClient { okHttpClient }
        .memoryCache {
            MemoryCache.Builder(context).maxSizePercent(0.25).build()
        }
        .diskCache {
            DiskCache.Builder()
                .directory(context.cacheDir.resolve("image_cache"))
                .maxSizeBytes(CoilConfig.CACHE_BYTES)
                .build()
        }
        .crossfade(true)
        .respectCacheHeaders(true)
        .memoryCachePolicy(CachePolicy.ENABLED)
        .diskCachePolicy(CachePolicy.ENABLED)
        .build()
}
