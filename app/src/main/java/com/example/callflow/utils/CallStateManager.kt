package com.example.callflow.utils

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.*

object CallStateManager {
    private val _isCallActive = MutableStateFlow(false)
    val isCallActive = _isCallActive.asStateFlow()

    private val _callDuration = MutableStateFlow("00:00")
    val callDuration = _callDuration.asStateFlow()

    private var timerJob: Job? = null
    private var startTime: Long = 0

    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    fun onCallStarted() {
        if (_isCallActive.value) return
        _isCallActive.value = true
        startTime = System.currentTimeMillis()
        startTimer()
    }

    fun onCallEnded() {
        _isCallActive.value = false
        stopTimer()
        _callDuration.value = "00:00"
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = scope.launch {
            while (isActive) {
                val elapsed = System.currentTimeMillis() - startTime
                val seconds = (elapsed / 1000) % 60
                val minutes = (elapsed / (1000 * 60)) % 60
                _callDuration.value = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
                delay(1000)
            }
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
        timerJob = null
    }
}
