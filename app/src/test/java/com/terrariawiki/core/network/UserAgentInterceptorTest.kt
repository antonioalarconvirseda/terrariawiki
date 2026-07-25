package com.terrariawiki.core.network

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import java.util.concurrent.TimeUnit

class UserAgentInterceptorTest {

    private lateinit var server: MockWebServer
    private lateinit var client: OkHttpClient

    @Before
    fun setup() {
        server = MockWebServer().apply { start() }
        client = OkHttpClient.Builder()
            .addInterceptor(UserAgentInterceptor(userAgent = "TestAgent/1.0", maxRetries429 = 0))
            .callTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun `adds User-Agent header when request has none`() {
        server.enqueue(MockResponse().setResponseCode(200).setBody("OK"))
        val request = Request.Builder().url(server.url("/img.png")).build()
        client.newCall(request).execute().close()

        val recorded = server.takeRequest()
        assertEquals("TestAgent/1.0", recorded.getHeader("User-Agent"))
    }

    @Test
    fun `preserves existing User-Agent header`() {
        server.enqueue(MockResponse().setResponseCode(200).setBody("OK"))
        val request = Request.Builder()
            .url(server.url("/img.png"))
            .header("User-Agent", "Custom/2.0")
            .build()
        client.newCall(request).execute().close()

        val recorded = server.takeRequest()
        assertEquals("Custom/2.0", recorded.getHeader("User-Agent"))
    }
}
