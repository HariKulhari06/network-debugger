package com.hari.tracea.core.util

import platform.Foundation.*
import kotlinx.cinterop.ExperimentalForeignApi

actual fun generateUuid(): String = NSUUID().UUIDString()

actual fun formatTime(timestamp: Long): String {
    val formatter = NSDateFormatter().apply {
        dateFormat = "HH:mm:ss"
        locale = NSLocale.currentLocale
    }
    // Convert 1970 timestamp to 2001 reference date
    val timeIntervalSince2001 = (timestamp.toDouble() / 1000.0) - 978307200.0
    val date = NSDate(timeIntervalSinceReferenceDate = timeIntervalSince2001)
    return formatter.stringFromDate(date)
}

actual fun formatFullDate(timestamp: Long): String {
    val formatter = NSDateFormatter().apply {
        dateFormat = "yyyy-MM-dd HH:mm:ss"
        locale = NSLocale.currentLocale
    }
    val timeIntervalSince2001 = (timestamp.toDouble() / 1000.0) - 978307200.0
    val date = NSDate(timeIntervalSinceReferenceDate = timeIntervalSince2001)
    return formatter.stringFromDate(date)
}

@OptIn(ExperimentalForeignApi::class)
actual fun getFilesDirPath(context: Any?): String {
    val paths = NSFileManager.defaultManager.URLsForDirectory(NSDocumentDirectory, NSUserDomainMask)
    val documentsUrl = paths.first() as NSURL
    val traceaPath = (documentsUrl.path ?: "") + "/tracea"
    NSFileManager.defaultManager.createDirectoryAtPath(traceaPath, true, null, null)
    return traceaPath
}

@OptIn(ExperimentalForeignApi::class)
actual fun writeTextToFile(dirPath: String, fileName: String, text: String) {
    val filePath = "$dirPath/$fileName"
    val nsString = NSString.create(string = text)
    nsString.writeToFile(filePath, true, NSUTF8StringEncoding, null)
}

@OptIn(ExperimentalForeignApi::class)
actual fun readTextFromFile(dirPath: String, fileName: String): String? {
    val filePath = "$dirPath/$fileName"
    if (!NSFileManager.defaultManager.fileExistsAtPath(filePath)) return null
    return NSString.create(contentsOfFile = filePath, encoding = NSUTF8StringEncoding, error = null) as? String
}

@OptIn(ExperimentalForeignApi::class)
actual fun currentTimeMillis(): Long {
    // POSIX time returns seconds, convert to milliseconds
    return platform.posix.time(null) * 1000L
}

actual fun mapThrowableToNetworkError(throwable: Throwable): com.hari.tracea.core.model.NetworkError {
    val type = when (throwable) {
        is kotlinx.coroutines.CancellationException -> com.hari.tracea.core.model.ErrorType.CANCELLED
        else -> com.hari.tracea.core.model.ErrorType.UNKNOWN
    }
    return com.hari.tracea.core.model.NetworkError(
        type = type,
        message = throwable.message,
        throwableClassName = throwable::class.simpleName
    )
}

actual fun formatIsoDateTime(timestamp: Long): String {
    val formatter = NSDateFormatter().apply {
        dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
        timeZone = NSTimeZone.timeZoneWithName(tzName = "UTC") ?: NSTimeZone.defaultTimeZone()
        locale = NSLocale(localeIdentifier = "en_US_POSIX")
    }
    val timeIntervalSince2001 = (timestamp.toDouble() / 1000.0) - 978307200.0
    val date = NSDate(timeIntervalSinceReferenceDate = timeIntervalSince2001)
    return formatter.stringFromDate(date)
}

@OptIn(ExperimentalForeignApi::class)
actual fun deleteFile(dirPath: String, fileName: String): Boolean {
    val filePath = "$dirPath/$fileName"
    if (!NSFileManager.defaultManager.fileExistsAtPath(filePath)) return false
    return NSFileManager.defaultManager.removeItemAtPath(filePath, null)
}

@OptIn(ExperimentalForeignApi::class)
actual fun deleteDirectoryContents(dirPath: String) {
    val fileManager = NSFileManager.defaultManager
    val contents = fileManager.contentsOfDirectoryAtPath(dirPath, null) as? List<String> ?: return
    for (item in contents) {
        val filePath = "$dirPath/$item"
        fileManager.removeItemAtPath(filePath, null)
    }
}
