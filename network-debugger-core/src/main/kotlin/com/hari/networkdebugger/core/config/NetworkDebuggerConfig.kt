package com.hari.networkdebugger.core.config

/**
 * Main configuration for Network Debugger.
 */
data class NetworkDebuggerConfig(
    val enabled: Boolean = true,
    val bodyCaptureConfig: BodyCaptureConfig = BodyCaptureConfig(),
    val storageConfig: StorageConfig = StorageConfig(),
    val redactionConfig: RedactionConfig = RedactionConfig(),
    val showFloatingButton: Boolean = true
)
