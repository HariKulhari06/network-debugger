package com.hari.networkdebugger.ui

import com.hari.networkdebugger.core.config.NetworkDebuggerConfig
import com.hari.networkdebugger.core.store.NetworkEventStore

object DebuggerServiceLocator {
    var store: NetworkEventStore? = null
    var config: NetworkDebuggerConfig? = null
}
