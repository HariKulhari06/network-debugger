package com.hari.tracea.core.util

import com.hari.tracea.core.model.NetworkError

expect fun generateUuid(): String
expect fun formatTime(timestamp: Long): String
expect fun formatFullDate(timestamp: Long): String
expect fun getFilesDirPath(context: Any?): String
expect fun writeTextToFile(dirPath: String, fileName: String, text: String)
expect fun readTextFromFile(dirPath: String, fileName: String): String?
expect fun currentTimeMillis(): Long
expect fun mapThrowableToNetworkError(throwable: Throwable): NetworkError
expect fun formatIsoDateTime(timestamp: Long): String
