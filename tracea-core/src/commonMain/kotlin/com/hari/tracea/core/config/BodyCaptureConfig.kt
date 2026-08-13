package com.hari.tracea.core.config

/**
 * Configuration for body capturing.
 */
data class BodyCaptureConfig(
    val enabled: Boolean = true,
    val maxRequestBodySize: Long = 1L * 1024 * 1024,
    val maxResponseBodySize: Long = 2L * 1024 * 1024,
    val captureBinary: Boolean = false
)
