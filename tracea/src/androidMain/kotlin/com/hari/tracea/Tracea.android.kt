package com.hari.tracea

import android.content.Context
import android.content.Intent
import com.hari.tracea.ui.TraceaServiceLocator
import com.hari.tracea.ui.TraceaActivity
import com.hari.tracea.okhttp.TraceaInterceptor
import com.hari.tracea.core.model.DebuggerSession

actual fun platformInit(context: Any?) {
    TraceaServiceLocator.store = Tracea._store
    TraceaServiceLocator.config = Tracea._config
    DebuggerSession.startNewSession()
    TraceaServiceLocator.sessionId = DebuggerSession.sessionId
    TraceaServiceLocator.sessionName = DebuggerSession.sessionName
}

private var _interceptor: TraceaInterceptor? = null

/**
 * Get an OkHttp Interceptor that automatically captures network requests.
 */
fun Tracea.okHttpInterceptor(): okhttp3.Interceptor {
    check(initialized) { "Tracea.initialize() must be called first" }
    if (!_config.enabled) {
        // Return a no-op interceptor
        return okhttp3.Interceptor { chain -> chain.proceed(chain.request()) }
    }
    if (_interceptor == null) {
        _interceptor = TraceaInterceptor(_collector, _config)
    }
    return _interceptor!!
}

/**
 * Open the Tracea UI.
 */
fun Tracea.show(context: Context) {
    check(initialized) { "Tracea.initialize() must be called first" }
    val intent = Intent(context, TraceaActivity::class.java)
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    context.startActivity(intent)
}
