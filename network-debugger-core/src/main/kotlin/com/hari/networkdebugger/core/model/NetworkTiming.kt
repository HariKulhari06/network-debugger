package com.hari.networkdebugger.core.model

/**
 * Timing metrics for a network request.
 */
data class NetworkTiming(
    val startTimestamp: Long,
    val endTimestamp: Long? = null,
    val dnsMs: Long? = null,
    val connectMs: Long? = null,
    val tlsMs: Long? = null,
    val waitingMs: Long? = null,
    val downloadMs: Long? = null
) {
    /** Total request time from start to finish, if finished. */
    val totalMs: Long?
        get() = endTimestamp?.let { it - startTimestamp }
}
