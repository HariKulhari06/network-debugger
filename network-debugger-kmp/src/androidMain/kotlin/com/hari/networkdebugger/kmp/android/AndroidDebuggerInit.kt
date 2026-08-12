package com.hari.networkdebugger.kmp.android

import android.content.Context
import com.hari.networkdebugger.NetworkDebugger
import com.hari.networkdebugger.core.config.NetworkDebuggerConfig

object AndroidDebuggerInit {
    fun initialize(context: Context, enabled: Boolean = true, showFloatingButton: Boolean = true) {
        NetworkDebugger.initialize(
            context = context,
            config = NetworkDebuggerConfig(
                enabled = enabled,
                showFloatingButton = showFloatingButton
            )
        )
    }
}
