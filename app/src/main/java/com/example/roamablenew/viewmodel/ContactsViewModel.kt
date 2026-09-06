package com.example.roamablenew.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.roamablenew.data.Contact
import com.example.roamablenew.data.ContactCategory
import com.example.roamablenew.data.ContactsRepository
import com.example.roamablenew.data.SupabaseContactsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ContactsViewModel(
    private val repository: ContactsRepository =
        SupabaseContactsRepository()
) : ViewModel() {

    private val _contacts =
        MutableStateFlow<List<Contact>>(emptyList())

    val contacts: StateFlow<List<Contact>> =
        _contacts.asStateFlow()

    private val _errorMessage =
        MutableStateFlow<String?>(null)

    val errorMessage: StateFlow<String?> =
        _errorMessage.asStateFlow()

    init {
        loadContacts()
    }

    fun loadContacts() {

        viewModelScope.launch {

            try {

                _contacts.value =
                    repository.getAll()

                _errorMessage.value = null

            } catch (e: Exception) {

                _errorMessage.value =
                    "Failed to load contacts: ${e.message}"

                e.printStackTrace()
            }
        }
    }

    fun addContact(
        name: String,
        category: ContactCategory,
        phone: String,
        address: String,
        notes: String
    ) {

        viewModelScope.launch {

            try {

                val contact = Contact(
                    name = name,
                    category = category,
                    phoneNumber = phone,
                    address = address,
                    notes = notes
                )

                repository.add(contact)

                loadContacts()

            } catch (e: Exception) {

                _errorMessage.value =
                    "Failed to add contact: ${e.message}"

                e.printStackTrace()
            }
        }
    }

    fun updateContact(contact: Contact) {

        viewModelScope.launch {

            try {

                repository.update(contact)

                loadContacts()

            } catch (e: Exception) {

                _errorMessage.value =
                    "Failed to update contact: ${e.message}"

                e.printStackTrace()
            }
        }
    }

    fun removeContact(contactId: String) {

        viewModelScope.launch {

            try {

                repository.remove(contactId)

                loadContacts()

            } catch (e: Exception) {

                _errorMessage.value =
                    "Failed to remove contact: ${e.message}"

                e.printStackTrace()
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}