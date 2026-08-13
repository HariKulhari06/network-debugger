package com.hari.tracea.manual.util

import java.net.URI
import java.net.URLDecoder

actual fun parseUrl(url: String): ParsedUrl {
    return try {
        val uri = URI(url)
        val scheme = uri.scheme.orEmpty()
        val host = uri.host.orEmpty()
        val port = if (uri.port != -1) uri.port else null
        val path = uri.path.orEmpty()
        val queryParams = parseQueryParams(uri.rawQuery)
        ParsedUrl(scheme, host, port, path, queryParams)
    } catch (e: Exception) {
        ParsedUrl(
            scheme = "",
            host = "",
            port = null,
            path = url,
            queryParameters = emptyMap()
        )
    }
}

private fun parseQueryParams(rawQuery: String?): Map<String, List<String>> {
    if (rawQuery.isNullOrEmpty()) return emptyMap()
    val map = mutableMapOf<String, MutableList<String>>()
    rawQuery.split("&").forEach { param ->
        if (param.isNotEmpty()) {
            val parts = param.split("=", limit = 2)
            val key = try {
                URLDecoder.decode(parts[0], "UTF-8")
            } catch (e: Exception) {
                parts[0]
            }
            val value = if (parts.size > 1) {
                try {
                    URLDecoder.decode(parts[1], "UTF-8")
                } catch (e: Exception) {
                    parts[1]
                }
            } else ""
            map.getOrPut(key) { mutableListOf() }.add(value)
        }
    }
    return map
}
