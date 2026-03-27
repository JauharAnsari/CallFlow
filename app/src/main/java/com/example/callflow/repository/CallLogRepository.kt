package com.example.callflow.repository

import android.content.Context
import android.provider.CallLog
import com.example.callflow.data.CallLogEntry
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CallLogRepository(private val context: Context) {
    fun fetchCallLogs(): List<CallLogEntry> {
        val callLogs = mutableListOf<CallLogEntry>()
        val contentResolver = context.contentResolver
        val cursor = contentResolver.query(
            CallLog.Calls.CONTENT_URI,
            null,
            null,
            null,
            CallLog.Calls.DATE + " DESC"
        )

        cursor?.use {
            val idIndex = it.getColumnIndex(CallLog.Calls._ID)
            val nameIndex = it.getColumnIndex(CallLog.Calls.CACHED_NAME)
            val numberIndex = it.getColumnIndex(CallLog.Calls.NUMBER)
            val typeIndex = it.getColumnIndex(CallLog.Calls.TYPE)
            val dateIndex = it.getColumnIndex(CallLog.Calls.DATE)
            val durationIndex = it.getColumnIndex(CallLog.Calls.DURATION)

            while (it.moveToNext()) {
                val id = it.getString(idIndex) ?: ""
                val name = it.getString(nameIndex)
                val number = it.getString(numberIndex) ?: "Unknown"
                val type = it.getInt(typeIndex)
                val date = it.getLong(dateIndex)
                val durationSeconds = it.getInt(durationIndex)
                val durationFormatted = formatDuration(durationSeconds)

                callLogs.add(CallLogEntry(id, name, number, type, date, durationFormatted))
            }
        }
        return callLogs
    }

    private fun formatDuration(seconds: Int): String {
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, remainingSeconds)
    }
}
