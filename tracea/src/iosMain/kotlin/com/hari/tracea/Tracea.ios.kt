package com.hari.tracea

import com.hari.tracea.core.model.DebuggerSession

actual fun platformInit(context: Any?) {
    DebuggerSession.startNewSession()
}
