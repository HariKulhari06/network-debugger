package com.hari.tracea.core.util

import kotlin.math.round

/**
 * Formats durations in milliseconds.
 */
object DurationFormatter {
    fun format(ms: Long): String {
        return when {
            ms < 1000 -> "$ms ms"
            ms < 60000 -> {
                val sec = ms / 1000.0
                "${round(sec * 100.0) / 100.0} s"
            }
            else -> {
                val minutes = ms / 60000
                val seconds = (ms % 60000) / 1000
                "${minutes}m ${seconds}s"
            }
        }
    }
}
