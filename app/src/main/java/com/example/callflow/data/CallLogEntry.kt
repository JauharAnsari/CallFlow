package com.example.callflow.data

data class CallLogEntry(
    val id: String,
    val name: String?,
    val number: String,
    val type: Int, // CallLog.Calls.INCOMING_TYPE, etc.
    val date: Long,
    val duration: String
)
