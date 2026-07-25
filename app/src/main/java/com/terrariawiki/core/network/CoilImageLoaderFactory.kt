package com.terrariawiki.core.network

import android.content.Context
import android.util.Log
import coil.ImageLoader
import coil.disk.DiskCache
import coil.memory.MemoryCache
import coil.request.CachePolicy
import okhttp3.Dispatcher
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response

object CoilConfig {
    const val LOG_TAG = "CoilHttp"
    const val MAX_CONCURRENT_PER_HOST = 5
    const val MAX_TOTAL_REQUESTS = 20
    const val CACHE_BYTES = 50L * 1024 * 1024
    const val MAX_RETRIES_429 = 3
    const val INITIAL_BACKOFF_MS = 1_000L
}

class RateLimitInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val url = request.url.toString()
        var response = chain.proceed(request)
        Log.d(CoilConfig.LOG_TAG, "${response.code} ${url.take(120)}")
        var attempts = 0
        while (response.code == 429 && attempts < CoilConfig.MAX_RETRIES_429) {
            val retryAfter = response.header("Retry-After")?.toLongOrNull()
            val backoffMs = retryAfter?.times(1000) ?: (CoilConfig.INITIAL_BACKOFF_MS shl attempts)
            Log.w(
                CoilConfig.LOG_TAG,
                "429 rate-limited, retrying in ${backoffMs}ms (attempt ${attempts + 1}/${CoilConfig.MAX_RETRIES_429}) ${url.take(80)}"
            )
            response.close()
            Thread.sleep(backoffMs)
            response = chain.proceed(request)
            Log.d(CoilConfig.LOG_TAG, "retry -> ${response.code} ${url.take(120)}")
            attempts++
        }
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
        .addInterceptor(RateLimitInterceptor())
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
