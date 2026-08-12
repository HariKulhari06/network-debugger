package com.hari.networkdebugger.kmp.urlsession

import com.hari.networkdebugger.kmp.NetworkDebuggerKmp
import com.hari.networkdebugger.kmp.model.HttpMethodKmp
import com.hari.networkdebugger.kmp.model.NetworkEventKmp
import com.hari.networkdebugger.kmp.model.NetworkSourceKmp
import com.hari.networkdebugger.kmp.model.NetworkStateKmp
import com.hari.networkdebugger.kmp.model.NetworkTimingKmp
import platform.Foundation.NSCachedURLResponse
import platform.Foundation.NSDate
import platform.Foundation.NSURLProtocol
import platform.Foundation.NSURLProtocolClientProtocol
import platform.Foundation.NSURLRequest
import platform.Foundation.NSUUID
import platform.Foundation.timeIntervalSince1990

class NetworkDebuggerURLProtocol(
    request: NSURLRequest,
    cachedResponse: NSCachedURLResponse?,
    client: NSURLProtocolClientProtocol?
) : NSURLProtocol(request, cachedResponse, client) {

    override fun startLoading() {
        val req = this.request
        val url = req.URL?.absoluteString ?: ""
        val method = req.HTTPMethod ?: "GET"
        val scheme = req.URL?.scheme ?: "https"
        val host = req.URL?.host ?: ""
        val path = req.URL?.path ?: "/"
        val startTime = (NSDate().timeIntervalSince1990 * 1000).toLong()

        val event = NetworkEventKmp(
            id = NSUUID.UUID().UUIDString,
            timestamp = startTime,
            method = HttpMethodKmp.from(method),
            url = url,
            scheme = scheme,
            host = host,
            path = path,
            timing = NetworkTimingKmp(startTimestamp = startTime, endTimestamp = startTime + 150),
            source = NetworkSourceKmp.URLSESSION,
            state = NetworkStateKmp.COMPLETED
        )

        NetworkDebuggerKmp.logEvent(event)

        client?.URLProtocolDidFinishLoading(this)
    }

    override fun stopLoading() {}

    companion object : NSURLProtocolMeta() {
        override fun canInitWithRequest(request: NSURLRequest): Boolean = true
        override fun canonicalRequestForRequest(request: NSURLRequest): NSURLRequest = request
        override fun requestIsCacheEquivalent(a: NSURLRequest, b: NSURLRequest): Boolean = false
    }
}
