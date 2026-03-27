package com.example.callflow.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.callflow.data.Contact
import com.example.callflow.repository.ContactsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ContactsViewModel(private val repository: ContactsRepository) : ViewModel() {
    private val _contacts = MutableStateFlow<List<Contact>>(emptyList())
    val contacts: StateFlow<List<Contact>> = _contacts.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    fun loadContacts() {
        viewModelScope.launch {
            _contacts.value = repository.fetchContacts()
        }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun getFilteredContacts(): List<Contact> {
        val query = _searchQuery.value.lowercase()
        return if (query.isEmpty()) {
            _contacts.value
        } else {
            _contacts.value.filter {
                it.name.lowercase().contains(query) || it.number.contains(query)
            }
        }
    }
}
