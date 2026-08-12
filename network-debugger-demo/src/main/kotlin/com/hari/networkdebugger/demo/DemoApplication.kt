package com.hari.networkdebugger.demo

import android.app.Application
import com.hari.networkdebugger.NetworkDebugger
import com.hari.networkdebugger.core.config.NetworkDebuggerConfig
import com.hari.networkdebugger.core.config.RedactionConfig

class DemoApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        NetworkDebugger.initialize(
            context = this,
            config = NetworkDebuggerConfig(
                enabled = BuildConfig.DEBUG,
                redactionConfig = RedactionConfig(
                    sensitiveHeaders = setOf("Authorization", "Cookie", "Set-Cookie", "Proxy-Authorization", "X-API-Key"),
                    sensitiveJsonFields = setOf("password", "token", "access_token", "refresh_token", "secret", "client_secret", "api_key")
                )
            )
        )
    }
}
