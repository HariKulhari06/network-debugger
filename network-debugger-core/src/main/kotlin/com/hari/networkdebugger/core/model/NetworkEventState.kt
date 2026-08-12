package com.hari.networkdebugger.core.model

/**
 * The state of a network event lifecycle.
 */
enum class NetworkEventState {
    STARTED, REQUEST_CAPTURED, RESPONSE_RECEIVED, COMPLETED, FAILED, CANCELLED
}
