package com.example.callflow.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.callflow.utils.CallStateManager
import com.example.callflow.data.Contact
import com.example.callflow.repository.ContactsRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class DialerViewModel(private val repository: ContactsRepository) : ViewModel() {
    private val _phoneNumber = MutableStateFlow("")
    val phoneNumber: StateFlow<String> = _phoneNumber.asStateFlow()

    private val _contacts = MutableStateFlow<List<Contact>>(emptyList())

    val matchedName: StateFlow<String?> = combine(_phoneNumber, _contacts) { number, contacts ->
        if (number.isEmpty()) return@combine null
        
        val normalizedInput = number.replace(Regex("[^0-9+]"), "")
        contacts.find { 
            it.number.replace(Regex("[^0-9+]"), "").contains(normalizedInput) ||
            normalizedInput.contains(it.number.replace(Regex("[^0-9+]"), ""))
        }?.name
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val callDuration = CallStateManager.callDuration
    val isCallActive = CallStateManager.isCallActive

    init {
        loadContacts()
    }

    private fun loadContacts() {
        viewModelScope.launch {
            _contacts.value = repository.fetchContacts()
        }
    }

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
