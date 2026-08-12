package com.hari.networkdebugger.manual

import com.hari.networkdebugger.core.config.NetworkDebuggerConfig
import com.hari.networkdebugger.core.model.HttpMethod
import com.hari.networkdebugger.core.pipeline.NetworkEventCollector
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import java.util.UUID

/**
 * Entry point API for manually capturing network calls.
 *
 * @property collector Pipeline collector to process network events
 * @property config Network Debugger configuration settings
 */
class ManualCaptureApi(
    private val collector: NetworkEventCollector,
    private val config: NetworkDebuggerConfig
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    /**
     * Starts tracking a manual network request.
     *
     * @param method HTTP method string (e.g., "GET", "POST", "PUT", "DELETE")
     * @param url Full target URL of the network call
     * @return A builder [ManualNetworkCall] instance to record request and response details
     */
    fun startRequest(method: String, url: String): ManualNetworkCall {
        val id = UUID.randomUUID().toString()
        return ManualNetworkCall(
            id = id,
            method = HttpMethod.from(method),
            url = url,
            collector = collector,
            config = config,
            scope = scope
        )
    }
}
