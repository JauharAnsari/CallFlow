package com.example.callflow.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.callflow.data.Contact
import com.example.callflow.repository.ContactsRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ContactsViewModel(private val repository: ContactsRepository) : ViewModel() {
    private val _contacts = MutableStateFlow<List<Contact>>(emptyList())
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Combined flow for filtered contacts
    val filteredContacts: StateFlow<List<Contact>> = combine(_contacts, _searchQuery) { contacts, query ->
        if (query.isEmpty()) {
            contacts
        } else {
            val lowerQuery = query.lowercase()
            contacts.filter {
                it.name.lowercase().contains(lowerQuery) || it.number.contains(lowerQuery)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun loadContacts() {
        if (_contacts.value.isNotEmpty()) return // Already loaded
        
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _contacts.value = repository.fetchContacts()
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }
}
