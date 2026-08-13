package com.hari.tracea.core.util

import android.content.Context
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

actual fun generateUuid(): String = UUID.randomUUID().toString()

actual fun formatTime(timestamp: Long): String {
    return SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date(timestamp))
}

actual fun formatFullDate(timestamp: Long): String {
    return SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date(timestamp))
}

actual fun getFilesDirPath(context: Any?): String {
    val ctx = context as Context
    val dir = java.io.File(ctx.filesDir, "tracea")
    dir.mkdirs()
    return dir.absolutePath
}

actual fun writeTextToFile(dirPath: String, fileName: String, text: String) {
    try {
        val file = java.io.File(dirPath, fileName)
        file.writeText(text)
    } catch (e: Exception) {
        // Ignore
    }
}

actual fun readTextFromFile(dirPath: String, fileName: String): String? {
    return try {
        val file = java.io.File(dirPath, fileName)
        if (file.exists()) file.readText() else null
    } catch (e: Exception) {
        null
    }
}

actual fun currentTimeMillis(): Long = System.currentTimeMillis()

actual fun mapThrowableToNetworkError(throwable: Throwable): com.hari.tracea.core.model.NetworkError {
    val type = when (throwable) {
        is java.net.SocketTimeoutException -> com.hari.tracea.core.model.ErrorType.TIMEOUT
        is java.net.UnknownHostException -> com.hari.tracea.core.model.ErrorType.DNS_FAILURE
        is java.net.ConnectException -> com.hari.tracea.core.model.ErrorType.CONNECTION_FAILURE
        is javax.net.ssl.SSLException -> com.hari.tracea.core.model.ErrorType.TLS_ERROR
        is java.util.concurrent.CancellationException -> com.hari.tracea.core.model.ErrorType.CANCELLED
        is java.io.IOException -> com.hari.tracea.core.model.ErrorType.IO_ERROR
        else -> com.hari.tracea.core.model.ErrorType.UNKNOWN
    }
    return com.hari.tracea.core.model.NetworkError(
        type = type,
        message = throwable.message,
        throwableClassName = throwable::class.qualifiedName
    )
}

actual fun formatIsoDateTime(timestamp: Long): String {
    val format = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", java.util.Locale.US).apply {
        timeZone = java.util.TimeZone.getTimeZone("UTC")
    }
    return format.format(java.util.Date(timestamp))
}

actual fun deleteFile(dirPath: String, fileName: String): Boolean {
    val file = java.io.File(dirPath, fileName)
    return if (file.exists()) file.delete() else false
}

actual fun deleteDirectoryContents(dirPath: String) {
    val dir = java.io.File(dirPath)
    dir.listFiles()?.forEach { it.delete() }
}
