package com.hari.tracea.ui.util

import androidx.core.content.FileProvider

/**
 * Internal FileProvider for Tracea to share HAR and transaction log files.
 * Uses a custom subclass to avoid meta-data and authority collisions with the host app
 * or other libraries using standard androidx.core.content.FileProvider.
 */
class TraceaFileProvider : FileProvider()
