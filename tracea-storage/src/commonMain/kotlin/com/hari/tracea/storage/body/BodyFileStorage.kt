package com.hari.tracea.storage.body

import com.hari.tracea.core.util.getFilesDirPath
import com.hari.tracea.core.util.writeTextToFile
import com.hari.tracea.core.util.readTextFromFile
import com.hari.tracea.core.util.deleteFile
import com.hari.tracea.core.util.deleteDirectoryContents

internal class BodyFileStorage(private val context: Any?) {
    private val bodiesDirPath: String by lazy {
        val baseDir = getFilesDirPath(context)
        val bodiesDir = "$baseDir/bodies"
        bodiesDir
    }

    fun writeBody(eventId: String, isRequest: Boolean, content: String): String {
        val size = content.encodeToByteArray().size
        if (size <= 4096) {
            return "inline:$content"
        }
        val prefix = if (isRequest) "req_" else "res_"
        val filename = "$prefix$eventId"
        writeTextToFile(bodiesDirPath, filename, content)
        return "file:$filename"
    }

    fun readBody(reference: String): String? {
        if (reference.startsWith("inline:")) {
            return reference.substringAfter("inline:")
        } else if (reference.startsWith("file:")) {
            val filename = reference.substringAfter("file:")
            return readTextFromFile(bodiesDirPath, filename)
        }
        return null
    }

    fun deleteBody(reference: String) {
        if (reference.startsWith("file:")) {
            val filename = reference.substringAfter("file:")
            deleteFile(bodiesDirPath, filename)
        }
    }

    fun deleteAllBodies() {
        deleteDirectoryContents(bodiesDirPath)
    }
}
