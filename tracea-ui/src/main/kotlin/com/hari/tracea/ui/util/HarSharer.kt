package com.hari.tracea.ui.util

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.content.FileProvider
import com.hari.tracea.core.model.NetworkEvent
import com.hari.tracea.core.util.HarExporter
import java.io.File

/**
 * Utility to export network events to a HAR file and launch the Android share sheet.
 */
object HarSharer {

    /**
     * Exports a list of [NetworkEvent]s to a HAR file and triggers the native system share sheet.
     */
    fun shareSessionHar(context: Context, sessionName: String, events: List<NetworkEvent>) {
        try {
            val harString = HarExporter.exportToHarString(events)
            val cacheDir = File(context.cacheDir, "shared_transactions")
            cacheDir.mkdirs()

            // Clean session name to avoid invalid characters in filename
            val safeName = sessionName.replace(Regex("[^a-zA-Z0-9]"), "_")
            val file = File(cacheDir, "${safeName}.har")
            file.writeText(harString)

            val authority = "${context.packageName}.tracea.fileprovider"
            val uri = FileProvider.getUriForFile(context, authority, file)

            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "application/json"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            
            val chooser = Intent.createChooser(intent, "Export HAR File").apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(chooser)
        } catch (e: Exception) {
            Toast.makeText(context, "Failed to export HAR: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}
