package com.example.callflow.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.callflow.utils.CallStateManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DialerViewModel : ViewModel() {
    private val _phoneNumber = MutableStateFlow("")
    val phoneNumber: StateFlow<String> = _phoneNumber.asStateFlow()

    val callDuration = CallStateManager.callDuration
    val isCallActive = CallStateManager.isCallActive

    fun onDigitClick(digit: String) {
        _phoneNumber.value += digit
    }

    fun onBackspaceClick() {
        if (_phoneNumber.value.isNotEmpty()) {
            _phoneNumber.value = _phoneNumber.value.dropLast(1)
        }
    }

    fun clearNumber() {
        _phoneNumber.value = ""
    }
}
