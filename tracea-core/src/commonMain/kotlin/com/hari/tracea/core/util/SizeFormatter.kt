package com.hari.tracea.core.util

import kotlin.math.round

/**
 * Formats byte sizes.
 */
object SizeFormatter {
    fun format(bytes: Long): String {
        if (bytes < 1024) return "$bytes B"
        val kb = bytes / 1024.0
        if (kb < 1024) return "${round(kb * 10.0) / 10.0} KB"
        val mb = kb / 1024.0
        if (mb < 1024) return "${round(mb * 10.0) / 10.0} MB"
        val gb = mb / 1024.0
        return "${round(gb * 10.0) / 10.0} GB"
    }
}
