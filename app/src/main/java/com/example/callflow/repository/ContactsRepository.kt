package com.example.callflow.repository

import android.content.Context
import android.provider.ContactsContract
import com.example.callflow.data.Contact
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ContactsRepository(private val context: Context) {
    suspend fun fetchContacts(): List<Contact> = withContext(Dispatchers.IO) {
        val contacts = mutableListOf<Contact>()
        val contentResolver = context.contentResolver
        
        // Define projection to fetch only necessary columns
        val projection = arrayOf(
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER
        )

        val cursor = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            projection,
            null,
            null,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
        )

        cursor?.use {
            val nameIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            val numberIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
            val idIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)

            while (it.moveToNext()) {
                val name = it.getString(nameIndex) ?: "Unknown"
                var number = it.getString(numberIndex) ?: ""
                val id = it.getString(idIndex) ?: ""
                
                // Basic normalization for comparison (remove spaces, etc.)
                val normalizedNumber = number.replace(Regex("[^0-9+]"), "")
                if (normalizedNumber.isNotEmpty()) {
                    contacts.add(Contact(id, name, number))
                }
            }
        }
        
        // Filter out duplicates (same name and primary number)
        contacts.distinctBy { "${it.name}|${it.number.replace(Regex("[^0-9+]"), "")}" }
    }
}
