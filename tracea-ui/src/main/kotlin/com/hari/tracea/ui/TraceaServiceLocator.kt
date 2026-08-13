package com.hari.tracea.ui

import com.hari.tracea.core.config.TraceaConfig
import com.hari.tracea.core.store.NetworkEventStore

object TraceaServiceLocator {
    var store: NetworkEventStore? = null
    var config: TraceaConfig? = null
    var sessionId: String = ""
    var sessionName: String = ""
}
