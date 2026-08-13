package com.hari.tracea.manual

import com.hari.tracea.core.config.TraceaConfig
import com.hari.tracea.core.model.HttpMethod
import com.hari.tracea.core.pipeline.NetworkEventCollector
import com.hari.tracea.core.util.generateUuid
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

/**
 * Entry point API for manually capturing network calls.
 *
 * @property collector Pipeline collector to process network events
 * @property config Tracea configuration settings
 */
class ManualCaptureApi(
    private val collector: NetworkEventCollector,
    private val config: TraceaConfig
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    /**
     * Starts tracking a manual network request.
     *
     * @param method HTTP method string (e.g., "GET", "POST", "PUT", "DELETE")
     * @param url Full target URL of the network call
     * @return A builder [ManualNetworkCall] instance to record request and response details
     */
    fun startRequest(method: String, url: String): ManualNetworkCall {
        val id = generateUuid()
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
