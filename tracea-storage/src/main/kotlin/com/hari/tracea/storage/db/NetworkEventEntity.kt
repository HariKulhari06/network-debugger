package com.hari.tracea.storage.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "network_events")
internal data class NetworkEventEntity(
    @PrimaryKey val id: String,
    val timestamp: Long,
    val method: String,
    val url: String,
    val host: String,
    val path: String,
    val scheme: String,
    val statusCode: Int?,
    val statusMessage: String?,
    val duration: Long?,
    val requestSize: Long,
    val responseSize: Long,
    val source: String,
    val state: String,
    val errorType: String?,
    val errorMessage: String?,
    val errorClassName: String?,
    val requestHeadersJson: String,
    val responseHeadersJson: String,
    val requestContentType: String?,
    val responseContentType: String?,
    val queryParametersJson: String,
    val protocol: String?,
    val port: Int?,
    val startTimestamp: Long,
    val endTimestamp: Long?,
    val dnsMs: Long?,
    val connectMs: Long?,
    val tlsMs: Long?,
    val waitingMs: Long?,
    val downloadMs: Long?,
    val requestBodyRef: String?,
    val responseBodyRef: String?,
    val requestBodyType: String?,
    val responseBodyType: String?,
    val requestBodySize: Long?,
    val responseBodySize: Long?,
    val sessionId: String,
    val sessionName: String
)
