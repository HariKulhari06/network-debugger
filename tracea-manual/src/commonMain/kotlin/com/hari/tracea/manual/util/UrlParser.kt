package com.hari.tracea.manual.util

data class ParsedUrl(
    val scheme: String,
    val host: String,
    val port: Int?,
    val path: String,
    val queryParameters: Map<String, List<String>>
)

expect fun parseUrl(url: String): ParsedUrl
