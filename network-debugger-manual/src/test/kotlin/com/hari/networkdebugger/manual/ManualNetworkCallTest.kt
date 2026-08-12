package com.hari.networkdebugger.manual

import com.hari.networkdebugger.core.config.NetworkDebuggerConfig
import com.hari.networkdebugger.core.model.BodyData
import com.hari.networkdebugger.core.model.ErrorType
import com.hari.networkdebugger.core.model.HttpMethod
import com.hari.networkdebugger.core.model.NetworkEventState
import com.hari.networkdebugger.core.model.NetworkSource
import com.hari.networkdebugger.core.pipeline.DefaultNetworkEventCollector
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ManualNetworkCallTest {

    @Test
    fun testSuccessfulManualNetworkCall() = runBlocking {
        val collector = DefaultNetworkEventCollector()
        val config = NetworkDebuggerConfig()
        val api = ManualCaptureApi(collector, config)

        val url = "https://api.example.com:8443/v1/users?page=1&sort=desc"
        val call = api.startRequest("POST", url)
            .requestHeaders(mapOf("Authorization" to "Bearer token123", "Content-Type" to "application/json"))
            .requestBody("""{"name":"John"}""", "application/json")
            .response(
                statusCode = 200,
                headers = mapOf("Content-Type" to "application/json"),
                body = """{"id":1,"name":"John"}""",
                contentType = "application/json"
            )

        assertNotNull(call)

        val event = collector.events.first()

        assertEquals(HttpMethod.POST, event.method)
        assertEquals(url, event.url)
        assertEquals("https", event.scheme)
        assertEquals("api.example.com", event.host)
        assertEquals(8443, event.port)
        assertEquals("/v1/users", event.path)
        assertEquals(listOf("1"), event.queryParameters["page"])
        assertEquals(listOf("desc"), event.queryParameters["sort"])
        assertEquals(NetworkSource.MANUAL, event.source)
        assertEquals(NetworkEventState.COMPLETED, event.state)
        assertEquals(200, event.statusCode)
        assertEquals("OK", event.statusMessage)
        assertEquals(listOf("Bearer token123"), event.requestHeaders["Authorization"])
        assertTrue(event.requestBody is BodyData.Text)
        assertEquals("""{"name":"John"}""", (event.requestBody as BodyData.Text).content)
        assertTrue(event.responseBody is BodyData.Text)
        assertEquals("""{"id":1,"name":"John"}""", (event.responseBody as BodyData.Text).content)
    }

    @Test
    fun testFailedManualNetworkCall() = runBlocking {
        val collector = DefaultNetworkEventCollector()
        val config = NetworkDebuggerConfig()
        val api = ManualCaptureApi(collector, config)

        val exception = java.io.IOException("Network connection lost")
        api.startRequest("GET", "https://api.example.com/data")
            .failure(exception)

        val event = collector.events.first()

        assertEquals(NetworkEventState.FAILED, event.state)
        assertNotNull(event.error)
        assertEquals(ErrorType.IO_ERROR, event.error?.type)
        assertEquals("Network connection lost", event.error?.message)
    }

    @Test
    fun testCancelledManualNetworkCall() = runBlocking {
        val collector = DefaultNetworkEventCollector()
        val config = NetworkDebuggerConfig()
        val api = ManualCaptureApi(collector, config)

        api.startRequest("GET", "https://api.example.com/data")
            .cancel()

        val event = collector.events.first()

        assertEquals(NetworkEventState.CANCELLED, event.state)
        assertNotNull(event.error)
        assertEquals(ErrorType.CANCELLED, event.error?.type)
    }
}
