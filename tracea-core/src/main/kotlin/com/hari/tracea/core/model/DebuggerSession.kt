package com.hari.tracea.core.model

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

/**
 * Manages the current debugging session ID and formatted name.
 */
public object DebuggerSession {
    
    @Volatile
    public var sessionId: String = UUID.randomUUID().toString()
        private set

    @Volatile
    public var sessionName: String = "Session: " + SimpleDateFormat("MMM dd, h:mm a", Locale.getDefault()).format(Date())
        private set

    /**
     * Creates a new unique session identifier and sets the formatted session name.
     */
    public fun startNewSession() {
        sessionId = UUID.randomUUID().toString()
        sessionName = "Session: " + SimpleDateFormat("MMM dd, h:mm a", Locale.getDefault()).format(Date())
    }
}
