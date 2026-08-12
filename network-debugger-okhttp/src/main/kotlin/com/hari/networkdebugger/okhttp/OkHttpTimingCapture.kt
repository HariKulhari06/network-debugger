package com.hari.networkdebugger.okhttp

import com.hari.networkdebugger.core.model.NetworkTiming
import okhttp3.Call
import okhttp3.EventListener
import okhttp3.Handshake
import okhttp3.Protocol
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Proxy
import java.util.concurrent.ConcurrentHashMap

/**
 * An EventListener.Factory that captures granular network timings.
 */
public class OkHttpTimingCapture : EventListener.Factory {

    private val timings = ConcurrentHashMap<Call, TimingData>()

    override fun create(call: Call): EventListener {
        return TimingEventListener(call, timings)
    }

    /**
     * Retrieves and removes the timing data for a specific call.
     */
    public fun getTimingForCall(call: Call): NetworkTiming? {
        val data = timings.remove(call) ?: return null
        
        val startMs = if (data.callStart > 0) data.callStart else System.currentTimeMillis()
        val endMs = if (data.callEnd > 0) data.callEnd else System.currentTimeMillis()
        val dnsMs = if (data.dnsStart > 0 && data.dnsEnd > 0) (data.dnsEnd - data.dnsStart) / 1_000_000L else null
        val connectMs = if (data.connectStart > 0 && data.connectEnd > 0) (data.connectEnd - data.connectStart) / 1_000_000L else null
        val tlsMs = if (data.secureConnectStart > 0 && data.secureConnectEnd > 0) (data.secureConnectEnd - data.secureConnectStart) / 1_000_000L else null
        val waitingMs = if (data.requestBodyEnd > 0 && data.responseHeadersStart > 0) (data.responseHeadersStart - data.requestBodyEnd) / 1_000_000L else null
        val downloadMs = if (data.responseHeadersStart > 0 && data.responseBodyEnd > 0) (data.responseBodyEnd - data.responseHeadersStart) / 1_000_000L else null

        return NetworkTiming(
            startTimestamp = startMs,
            endTimestamp = endMs,
            dnsMs = dnsMs?.coerceAtLeast(0),
            connectMs = connectMs?.coerceAtLeast(0),
            tlsMs = tlsMs?.coerceAtLeast(0),
            waitingMs = waitingMs?.coerceAtLeast(0),
            downloadMs = downloadMs?.coerceAtLeast(0)
        )
    }

    private class TimingData {
        var callStart: Long = System.currentTimeMillis()
        var callEnd: Long = 0
        var dnsStart: Long = 0
        var dnsEnd: Long = 0
        var connectStart: Long = 0
        var connectEnd: Long = 0
        var secureConnectStart: Long = 0
        var secureConnectEnd: Long = 0
        var requestBodyEnd: Long = 0
        var responseHeadersStart: Long = 0
        var responseBodyEnd: Long = 0
    }

    private class TimingEventListener(
        private val call: Call,
        private val timings: ConcurrentHashMap<Call, TimingData>
    ) : EventListener() {

        private val data = TimingData().also { timings[call] = it }

        override fun callStart(call: Call) {
            data.callStart = System.currentTimeMillis()
        }

        override fun dnsStart(call: Call, domainName: String) {
            data.dnsStart = System.nanoTime()
        }

        override fun dnsEnd(call: Call, domainName: String, inetAddressList: List<InetAddress>) {
            data.dnsEnd = System.nanoTime()
        }

        override fun connectStart(call: Call, inetSocketAddress: InetSocketAddress, proxy: Proxy) {
            data.connectStart = System.nanoTime()
        }

        override fun connectEnd(call: Call, inetSocketAddress: InetSocketAddress, proxy: Proxy, protocol: Protocol?) {
            data.connectEnd = System.nanoTime()
        }

        override fun connectFailed(call: Call, inetSocketAddress: InetSocketAddress, proxy: Proxy, protocol: Protocol?, ioe: java.io.IOException) {
            data.connectEnd = System.nanoTime()
        }

        override fun secureConnectStart(call: Call) {
            data.secureConnectStart = System.nanoTime()
        }

        override fun secureConnectEnd(call: Call, handshake: Handshake?) {
            data.secureConnectEnd = System.nanoTime()
        }

        override fun requestBodyEnd(call: Call, byteCount: Long) {
            data.requestBodyEnd = System.nanoTime()
        }
        
        override fun requestHeadersEnd(call: Call, request: okhttp3.Request) {
            if (data.requestBodyEnd == 0L) {
                data.requestBodyEnd = System.nanoTime()
            }
        }

        override fun responseHeadersStart(call: Call) {
            data.responseHeadersStart = System.nanoTime()
        }

        override fun responseBodyEnd(call: Call, byteCount: Long) {
            data.responseBodyEnd = System.nanoTime()
        }
        
        override fun callEnd(call: Call) {
            data.callEnd = System.currentTimeMillis()
            if (data.responseBodyEnd == 0L) {
                data.responseBodyEnd = System.nanoTime()
            }
        }
        
        override fun callFailed(call: Call, ioe: java.io.IOException) {
            data.callEnd = System.currentTimeMillis()
            if (data.responseBodyEnd == 0L) {
                data.responseBodyEnd = System.nanoTime()
            }
        }
    }
}
