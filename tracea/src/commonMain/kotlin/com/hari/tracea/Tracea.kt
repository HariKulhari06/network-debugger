package com.hari.tracea

import com.hari.tracea.core.config.TraceaConfig
import com.hari.tracea.core.pipeline.DefaultNetworkEventCollector
import com.hari.tracea.core.redaction.RedactionEngine
import com.hari.tracea.core.store.NetworkEventStore
import com.hari.tracea.manual.ManualCaptureApi
import com.hari.tracea.manual.ManualNetworkCall
import com.hari.tracea.storage.RoomNetworkEventStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

object Tracea {
    internal var initialized = false
    internal lateinit var _config: TraceaConfig
    internal lateinit var _collector: DefaultNetworkEventCollector
    internal lateinit var _store: NetworkEventStore
    internal lateinit var _redactionEngine: RedactionEngine
    internal lateinit var _manualApi: ManualCaptureApi
    internal val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    /**
     * Initialize the Tracea SDK.
     */
    fun initialize(context: Any? = null, config: TraceaConfig = TraceaConfig()) {
        if (initialized) return
        if (!config.enabled) {
            initialized = true
            _config = config
            return
        }
        
        _config = config
        _collector = DefaultNetworkEventCollector()
        _redactionEngine = RedactionEngine(config.redactionConfig)
        _store = RoomNetworkEventStore(context, config.storageConfig)
        _manualApi = ManualCaptureApi(_collector, config)
        
        com.hari.tracea.core.mock.MockEngine.initialize(com.hari.tracea.core.util.getFilesDirPath(context))
        
        // Execute platform-specific initialization hooks
        platformInit(context)
        
        // Start pipeline: collector events -> redaction -> store
        scope.launch {
            _collector.events.collect { event ->
                try {
                    val redactedEvent = _redactionEngine.redactEvent(event)
                    _store.insert(redactedEvent)
                } catch (e: Exception) {
                    // Silently handle storage errors
                }
            }
        }
        
        initialized = true
    }

    /**
     * Start tracking a manual network request.
     */
    fun startRequest(method: String, url: String): ManualNetworkCall {
        check(initialized) { "Tracea.initialize() must be called first" }
        return _manualApi.startRequest(method, url)
    }

    fun isEnabled(): Boolean = if (initialized) _config.enabled else false

    fun clear() {
        if (initialized && _config.enabled) {
            scope.launch { _store.clear() }
        }
    }
}

expect fun platformInit(context: Any?)
