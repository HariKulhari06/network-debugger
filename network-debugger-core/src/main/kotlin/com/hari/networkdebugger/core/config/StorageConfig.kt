package com.hari.networkdebugger.core.config

/**
 * Configuration for persistent storage.
 */
data class StorageConfig(
    val maxRequests: Int = 500,
    val maxBodySize: Long = 2L * 1024 * 1024
)
