package com.terrariawiki.core.network

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.util.concurrent.TimeUnit

class RateLimitInterceptorTest {

    private lateinit var server: MockWebServer
    private lateinit var client: OkHttpClient

    @Before
    fun setup() {
        server = MockWebServer().apply { start() }
        client = OkHttpClient.Builder()
            .addInterceptor(RateLimitInterceptor())
            .callTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun `passes through 200 response without retry`() {
        server.enqueue(MockResponse().setResponseCode(200).setBody("OK"))
        val request = Request.Builder().url(server.url("/test")).build()
        val response = client.newCall(request).execute()
        assertEquals(200, response.code)
        assertEquals(1, server.requestCount)
    }

    @Test
    fun `retries on 429 until success within max attempts`() {
        server.enqueue(MockResponse().setResponseCode(429))
        server.enqueue(MockResponse().setResponseCode(429))
        server.enqueue(MockResponse().setResponseCode(200).setBody("OK"))
        val request = Request.Builder().url(server.url("/test")).build()
        val response = client.newCall(request).execute()
        assertEquals(200, response.code)
        assertEquals(3, server.requestCount)
    }

    @Test
    fun `gives up after max retries and returns last 429`() {
        repeat(4) { server.enqueue(MockResponse().setResponseCode(429)) }
        val request = Request.Builder().url(server.url("/test")).build()
        val response = client.newCall(request).execute()
        assertEquals(429, response.code)
        assertEquals(4, server.requestCount)
    }

    @Test
    fun `does not retry on non-429 errors like 404`() {
        server.enqueue(MockResponse().setResponseCode(404))
        val request = Request.Builder().url(server.url("/missing")).build()
        val response = client.newCall(request).execute()
        assertEquals(404, response.code)
        assertEquals(1, server.requestCount)
    }
}
