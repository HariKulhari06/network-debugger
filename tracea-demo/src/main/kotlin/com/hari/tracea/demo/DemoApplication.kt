package com.hari.tracea.demo

import android.app.Application
import com.hari.tracea.Tracea
import com.hari.tracea.core.config.TraceaConfig
import com.hari.tracea.core.config.RedactionConfig

class DemoApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Tracea.initialize(
            context = this,
            config = TraceaConfig(
                enabled = BuildConfig.DEBUG,
                showFloatingButton = true,
                redactionConfig = RedactionConfig(
                    sensitiveHeaders = setOf("Authorization", "Cookie", "Set-Cookie", "Proxy-Authorization", "X-API-Key"),
                    sensitiveJsonFields = setOf("password", "token", "access_token", "refresh_token", "secret", "client_secret", "api_key")
                )
            )
        )
    }
}
