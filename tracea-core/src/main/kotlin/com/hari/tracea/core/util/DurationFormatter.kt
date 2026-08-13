package com.hari.tracea.core.util

/**
 * Formats durations in milliseconds.
 */
object DurationFormatter {
    fun format(ms: Long): String {
        return when {
            ms < 1000 -> "$ms ms"
            ms < 60000 -> {
                val sec = ms / 1000.0
                String.format(java.util.Locale.US, "%.2f s", sec)
            }
            else -> {
                val minutes = ms / 60000
                val seconds = (ms % 60000) / 1000
                "${minutes}m ${seconds}s"
            }
        }
    }
}
