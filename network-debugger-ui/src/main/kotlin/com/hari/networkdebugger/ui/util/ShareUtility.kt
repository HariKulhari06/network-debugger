package com.hari.networkdebugger.ui.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import com.hari.networkdebugger.core.model.BodyData
import com.hari.networkdebugger.core.model.NetworkEvent
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ShareUtility {

    fun generateFullReport(event: NetworkEvent): String {
        val sb = StringBuilder()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault())

        sb.append("=== NETWORK TRANSACTION REPORT ===\n")
        sb.append("Time: ${dateFormat.format(Date(event.timestamp))}\n")
        sb.append("URL: ${event.url}\n")
        sb.append("Method: ${event.method.name}\n")
        sb.append("Status: ${event.statusCode} ${event.statusMessage ?: ""}\n")
        sb.append("Duration: ${event.timing.totalMs ?: "N/A"} ms\n")
        sb.append("Size: ${event.requestSize + event.responseSize} bytes\n\n")

        sb.append("--- REQUEST HEADERS ---\n")
        event.requestHeaders.forEach { (key, values) ->
            sb.append("$key: ${values.joinToString(", ")}\n")
        }
        sb.append("\n")

        sb.append("--- REQUEST BODY ---\n")
        sb.append(formatBody(event.requestBody))
        sb.append("\n\n")

        sb.append("--- RESPONSE HEADERS ---\n")
        event.responseHeaders.forEach { (key, values) ->
            sb.append("$key: ${values.joinToString(", ")}\n")
        }
        sb.append("\n")

        sb.append("--- RESPONSE BODY ---\n")
        sb.append(formatBody(event.responseBody))
        sb.append("\n\n")

        sb.append("=== END OF REPORT ===")
        return sb.toString()
    }

    private fun formatBody(body: BodyData?): String {
        return when (body) {
            is BodyData.Text -> body.content
            is BodyData.Binary -> "[Binary Data: ${body.size} bytes]"
            is BodyData.Truncated -> "[Truncated Data: ${body.capturedSize} / ${body.actualSize} bytes]"
            is BodyData.FileReference -> "[File Reference: ${body.path}]"
            null -> "[Empty]"
        }
    }

    fun generateHar(event: NetworkEvent): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        dateFormat.timeZone = java.util.TimeZone.getTimeZone("UTC")
        
        val har = JSONObject()
        val log = JSONObject()
        har.put("log", log)
        log.put("version", "1.2")
        
        val creator = JSONObject()
        creator.put("name", "Network Debugger")
        creator.put("version", "1.0")
        log.put("creator", creator)
        
        val entries = JSONArray()
        log.put("entries", entries)
        
        val entry = JSONObject()
        entries.put(entry)
        
        entry.put("startedDateTime", dateFormat.format(Date(event.timestamp)))
        entry.put("time", event.timing.totalMs ?: 0L)
        
        // Request
        val request = JSONObject()
        entry.put("request", request)
        request.put("method", event.method.name)
        request.put("url", event.url)
        request.put("httpVersion", event.protocol ?: "HTTP/1.1")
        
        val reqHeaders = JSONArray()
        event.requestHeaders.forEach { (k, v) ->
            val h = JSONObject()
            h.put("name", k)
            h.put("value", v.joinToString(", "))
            reqHeaders.put(h)
        }
        request.put("headers", reqHeaders)
        
        val reqBody = event.requestBody
        if (reqBody is BodyData.Text) {
            val postData = JSONObject()
            postData.put("mimeType", event.requestContentType ?: "application/octet-stream")
            postData.put("text", reqBody.content)
            request.put("postData", postData)
        }
        
        // Response
        val response = JSONObject()
        entry.put("response", response)
        response.put("status", event.statusCode ?: 0)
        response.put("statusText", event.statusMessage ?: "")
        response.put("httpVersion", event.protocol ?: "HTTP/1.1")
        
        val resHeaders = JSONArray()
        event.responseHeaders.forEach { (k, v) ->
            val h = JSONObject()
            h.put("name", k)
            h.put("value", v.joinToString(", "))
            resHeaders.put(h)
        }
        response.put("headers", resHeaders)
        
        val content = JSONObject()
        content.put("size", event.responseSize)
        content.put("mimeType", event.responseContentType ?: "application/octet-stream")
        val resBody = event.responseBody
        if (resBody is BodyData.Text) {
            content.put("text", resBody.content)
        }
        response.put("content", content)
        
        // Timing
        val timings = JSONObject()
        timings.put("dns", event.timing.dnsMs ?: -1L)
        timings.put("connect", event.timing.connectMs ?: -1L)
        timings.put("ssl", event.timing.tlsMs ?: -1L)
        timings.put("wait", event.timing.waitingMs ?: -1L)
        timings.put("receive", event.timing.downloadMs ?: -1L)
        timings.put("send", 0)
        entry.put("timings", timings)
        
        return har.toString(2)
    }

    fun shareText(context: Context, text: String, title: String = "Share Network Transaction") {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
            putExtra(Intent.EXTRA_SUBJECT, title)
        }
        context.startActivity(Intent.createChooser(intent, title))
    }

    fun shareFile(context: Context, content: String, fileName: String, title: String) {
        try {
            val shareDir = File(context.cacheDir, "shared_transactions")
            if (!shareDir.exists()) shareDir.mkdirs()
            
            val file = File(shareDir, fileName)
            file.writeText(content)
            
            val uri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.networkdebugger.fileprovider",
                file
            )
            
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = context.contentResolver.getType(uri) ?: "application/octet-stream"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            
            context.startActivity(Intent.createChooser(intent, title))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
