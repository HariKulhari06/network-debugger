package com.hari.tracea.ui.overlay

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import com.hari.tracea.core.config.TraceaConfig
import com.hari.tracea.ui.TraceaActivity
import com.hari.tracea.ui.theme.DebuggerTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.lang.ref.WeakReference

object FloatingButtonManager : Application.ActivityLifecycleCallbacks {

    private const val TAG_OVERLAY_VIEW = "TRACEA_FLOATING_DEBUG_BUTTON_TAG"

    private var isInitialized = false
    private var applicationRef: WeakReference<Application>? = null
    private var currentActivityRef: WeakReference<Activity>? = null

    private val _isFloatingButtonEnabled = MutableStateFlow(true)
    val isFloatingButtonEnabled: StateFlow<Boolean> = _isFloatingButtonEnabled

    private val _requestCount = MutableStateFlow(0)
    val requestCount: StateFlow<Int> = _requestCount

    // Retain coordinates across activity switches
    private var lastOffsetX: Float = 16f
    private var lastOffsetY: Float = 200f

    fun init(application: Application, config: TraceaConfig) {
        if (isInitialized) return
        applicationRef = WeakReference(application)

        // Read user preference from SharedPreferences, fallback to TraceaConfig
        val prefs = application.getSharedPreferences("tracea_settings", Context.MODE_PRIVATE)
        val enabledInPrefs = prefs.getBoolean("floating_button", config.showFloatingButton)
        _isFloatingButtonEnabled.value = config.showFloatingButton && enabledInPrefs

        application.registerActivityLifecycleCallbacks(this)
        isInitialized = true
    }

    fun setEnabled(enabled: Boolean) {
        _isFloatingButtonEnabled.value = enabled
        val activity = currentActivityRef?.get() ?: return
        activity.runOnUiThread {
            if (enabled) {
                attachToActivity(activity)
            } else {
                detachFromActivity(activity)
            }
        }
    }

    fun updateRequestCount(count: Int) {
        _requestCount.value = count
    }

    fun showTracea(context: Context) {
        val intent = Intent(context, TraceaActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }

    private fun attachToActivity(activity: Activity) {
        if (!_isFloatingButtonEnabled.value) return
        if (activity is TraceaActivity) return // Never attach over Tracea UI itself
        if (activity.isFinishing || activity.isDestroyed) return

        val decorView = activity.window?.decorView as? ViewGroup ?: return
        val contentView = activity.findViewById<ViewGroup>(android.R.id.content) ?: decorView

        // Avoid adding duplicate overlay
        if (contentView.findViewWithTag<ComposeView>(TAG_OVERLAY_VIEW) != null) return

        val composeView = ComposeView(activity).apply {
            tag = TAG_OVERLAY_VIEW
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                val count by _requestCount.collectAsState()
                DebuggerTheme {
                    FloatingDebugButton(
                        requestCount = count,
                        initialOffsetX = lastOffsetX,
                        initialOffsetY = lastOffsetY,
                        onPositionChanged = { x, y ->
                            lastOffsetX = x
                            lastOffsetY = y
                        },
                        onClick = {
                            showTracea(activity)
                        }
                    )
                }
            }
        }

        val layoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
        }

        contentView.addView(composeView, layoutParams)
    }

    private fun detachFromActivity(activity: Activity) {
        val decorView = activity.window?.decorView as? ViewGroup
        val contentView = activity.findViewById<ViewGroup>(android.R.id.content) ?: decorView ?: return
        val existingView = contentView.findViewWithTag<ComposeView>(TAG_OVERLAY_VIEW)
        if (existingView != null) {
            contentView.removeView(existingView)
        }
    }

    override fun onActivityResumed(activity: Activity) {
        currentActivityRef = WeakReference(activity)
        attachToActivity(activity)
    }

    override fun onActivityPaused(activity: Activity) {
        detachFromActivity(activity)
        if (currentActivityRef?.get() === activity) {
            currentActivityRef = null
        }
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
    override fun onActivityStarted(activity: Activity) {}
    override fun onActivityStopped(activity: Activity) {}
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
    override fun onActivityDestroyed(activity: Activity) {
        detachFromActivity(activity)
    }
}
