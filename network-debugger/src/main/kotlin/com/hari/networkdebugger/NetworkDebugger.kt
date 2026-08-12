package com.hari.networkdebugger

import android.content.Context
import android.content.Intent
import com.hari.networkdebugger.core.config.NetworkDebuggerConfig
import com.hari.networkdebugger.core.pipeline.DefaultNetworkEventCollector
import com.hari.networkdebugger.core.redaction.RedactionEngine
import com.hari.networkdebugger.core.model.DebuggerSession
import com.hari.networkdebugger.core.store.NetworkEventStore
import com.hari.networkdebugger.manual.ManualCaptureApi
import com.hari.networkdebugger.manual.ManualNetworkCall
import com.hari.networkdebugger.okhttp.NetworkDebuggerInterceptor
import com.hari.networkdebugger.storage.RoomNetworkEventStore
import com.hari.networkdebugger.ui.DebuggerServiceLocator
import com.hari.networkdebugger.ui.NetworkDebuggerActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

object NetworkDebugger {
    private var initialized = false
    private lateinit var _config: NetworkDebuggerConfig
    private lateinit var _collector: DefaultNetworkEventCollector  // from core pipeline
    private lateinit var _store: NetworkEventStore
    private lateinit var _redactionEngine: RedactionEngine  // from core redaction
    private lateinit var _manualApi: ManualCaptureApi  // from manual module
    private var _interceptor: NetworkDebuggerInterceptor? = null  // from okhttp module
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    /**
     * Initialize the Network Debugger SDK. Call this in Application.onCreate().
     */
    fun initialize(context: Context, config: NetworkDebuggerConfig = NetworkDebuggerConfig()) {
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
        
        com.hari.networkdebugger.core.mock.MockEngine.initialize(context)
        
        // Set up the UI service locator
        DebuggerServiceLocator.store = _store
        DebuggerServiceLocator.config = config
        DebuggerSession.startNewSession()
        DebuggerServiceLocator.sessionId = DebuggerSession.sessionId
        DebuggerServiceLocator.sessionName = DebuggerSession.sessionName
        
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
     * Get an OkHttp Interceptor that automatically captures network requests.
     */
    fun okHttpInterceptor(): okhttp3.Interceptor {
        check(initialized) { "NetworkDebugger.initialize() must be called first" }
        if (!_config.enabled) {
            // Return a no-op interceptor
            return okhttp3.Interceptor { chain -> chain.proceed(chain.request()) }
        }
        if (_interceptor == null) {
            _interceptor = NetworkDebuggerInterceptor(_collector, _config)
        }
        return _interceptor!!
    }

    /**
     * Start tracking a manual network request.
     */
    fun startRequest(method: String, url: String): ManualNetworkCall {
        check(initialized) { "NetworkDebugger.initialize() must be called first" }
        return _manualApi.startRequest(method, url)
    }

    /**
     * Open the Network Debugger UI.
     */
    fun show(context: Context) {
        check(initialized) { "NetworkDebugger.initialize() must be called first" }
        val intent = Intent(context, NetworkDebuggerActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    fun isEnabled(): Boolean = if (initialized) _config.enabled else false

    fun clear() {
        if (initialized && _config.enabled) {
            scope.launch { _store.clear() }
        }
    }
}
