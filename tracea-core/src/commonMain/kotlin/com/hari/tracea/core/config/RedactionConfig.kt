package com.hari.tracea.core.config

/**
 * Configuration for data redaction.
 */
data class RedactionConfig(
    val sensitiveHeaders: Set<String> = setOf(
        "Authorization", "Cookie", "Set-Cookie", "Proxy-Authorization", "X-API-Key"
    ),
    val sensitiveJsonFields: Set<String> = setOf(
        "password", "token", "access_token", "refresh_token", "secret", "client_secret", "api_key"
    ),
    val replacementString: String = "[REDACTED]"
)
