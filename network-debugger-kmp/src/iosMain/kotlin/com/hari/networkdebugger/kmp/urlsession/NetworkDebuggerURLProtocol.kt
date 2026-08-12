package com.hari.networkdebugger.kmp.urlsession

import com.hari.networkdebugger.kmp.NetworkDebuggerKmp
import com.hari.networkdebugger.kmp.model.HttpMethodKmp
import com.hari.networkdebugger.kmp.model.NetworkEventKmp
import com.hari.networkdebugger.kmp.model.NetworkSourceKmp
import com.hari.networkdebugger.kmp.model.NetworkStateKmp
import com.hari.networkdebugger.kmp.model.NetworkTimingKmp
import platform.Foundation.NSHTTPURLResponse
import platform.Foundation.NSMutableURLRequest
import platform.Foundation.NSURL
import platform.Foundation.NSURLProtocol
import platform.Foundation.NSURLProtocolClientProtocol
import platform.Foundation.NSURLRequest
import platform.Foundation.NSURLResponse
import platform.Foundation.NSUUID

class NetworkDebuggerURLProtocol : NSURLProtocol {

    @Suppress("CONFLICTING_OVERLOADS")
    constructor(
        request: NSURLRequest,
        cachedResponse: platform.Foundation.NSCachedURLResponse?,
        client: NSURLProtocolClientProtocol?
    ) : super(request, cachedResponse, client)

    override fun startLoading() {
        val request = this.request
        val url = request.URL?.absoluteString ?: ""
        val method = request.HTTPMethod ?: "GET"
        val scheme = request.URL?.scheme ?: "https"
        val host = request.URL?.host ?: ""
        val path = request.URL?.path ?: "/"
        val startTime = platform.Foundation.NSDate().timeIntervalSince1990.toLong() * 1000

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

    companion me : NSURLProtocol.Companion() {
        override fun canInitWithRequest(request: NSURLRequest): Boolean = true
        override fun canonicalRequestForRequest(request: NSURLRequest): NSURLRequest = request
        override fun requestIsCacheEquivalent(a: NSURLRequest, b: NSURLRequest): Boolean = false
    }
}
