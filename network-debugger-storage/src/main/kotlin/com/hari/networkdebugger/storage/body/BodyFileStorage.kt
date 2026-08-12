package com.hari.networkdebugger.storage.body

import android.content.Context
import java.io.File

internal class BodyFileStorage(private val context: Context) {
    private val bodiesDir: File
        get() = File(context.filesDir, "network-debugger/bodies").also { it.mkdirs() }

    fun writeBody(eventId: String, isRequest: Boolean, content: String): String {
        val size = content.encodeToByteArray().size
        if (size <= 4096) {
            return "inline:$content"
        }
        val prefix = if (isRequest) "req_" else "res_"
        val filename = "$prefix$eventId"
        val file = File(bodiesDir, filename)
        file.writeText(content)
        return "file:$filename"
    }

    fun readBody(reference: String): String? {
        if (reference.startsWith("inline:")) {
            return reference.substringAfter("inline:")
        } else if (reference.startsWith("file:")) {
            val filename = reference.substringAfter("file:")
            val file = File(bodiesDir, filename)
            if (file.exists()) {
                return file.readText()
            }
        }
        return null
    }

    fun deleteBody(reference: String) {
        if (reference.startsWith("file:")) {
            val filename = reference.substringAfter("file:")
            val file = File(bodiesDir, filename)
            if (file.exists()) {
                file.delete()
            }
        }
    }

    fun deleteAllBodies() {
        bodiesDir.listFiles()?.forEach { it.delete() }
    }

    fun getBodyFile(reference: String): File? {
        if (reference.startsWith("file:")) {
            val filename = reference.substringAfter("file:")
            val file = File(bodiesDir, filename)
            if (file.exists()) return file
        }
        return null
    }
}
