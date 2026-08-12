package com.hari.networkdebugger.core.model

import kotlinx.serialization.Serializable

/**
 * Data class representing an API mock interception rule.
 */
@Serializable
data class MockRule(
    val id: String,
    val pathPattern: String,
    val method: HttpMethod,
    val statusCode: Int,
    val responseBody: String,
    val contentType: String = "application/json",
    val delayMs: Long = 0L,
    val enabled: Boolean = true
)
