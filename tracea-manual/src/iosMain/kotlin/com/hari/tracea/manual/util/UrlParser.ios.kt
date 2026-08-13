package com.hari.tracea.manual.util

import platform.Foundation.NSURL
import platform.Foundation.NSURLComponents
import platform.Foundation.NSURLQueryItem

actual fun parseUrl(url: String): ParsedUrl {
    val nsUrl = NSURL.URLWithString(url) ?: return ParsedUrl("", "", null, url, emptyMap())
    val scheme = nsUrl.scheme ?: ""
    val host = nsUrl.host ?: ""
    val port = nsUrl.port?.intValue
    val path = nsUrl.path ?: ""
    
    val components = NSURLComponents.componentsWithURL(nsUrl, resolvingAgainstBaseURL = true)
    val queryItems = components?.queryItems as? List<NSURLQueryItem>
    
    val queryParams = mutableMapOf<String, MutableList<String>>()
    queryItems?.forEach { item ->
        val name = item.name
        val value = item.value ?: ""
        queryParams.getOrPut(name) { mutableListOf() }.add(value)
    }
    
    return ParsedUrl(scheme, host, port, path, queryParams)
}
