package com.hari.tracea.core.model

import com.hari.tracea.core.util.generateUuid
import com.hari.tracea.core.util.formatFullDate
import com.hari.tracea.core.util.currentTimeMillis

/**
 * Manages the current debugging session ID and formatted name.
 */
public object DebuggerSession {
    
    public var sessionId: String = generateUuid()
        private set

    public var sessionName: String = "Session: " + formatFullDate(currentTimeMillis())
        private set

    /**
     * Creates a new unique session identifier and sets the formatted session name.
     */
    public fun startNewSession() {
        sessionId = generateUuid()
        sessionName = "Session: " + formatFullDate(currentTimeMillis())
    }
}
