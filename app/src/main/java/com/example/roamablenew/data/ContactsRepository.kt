package com.example.roamablenew.data

import io.github.jan.supabase.postgrest.from

interface ContactsRepository {

    suspend fun getAll(): List<Contact>

    suspend fun add(contact: Contact)

    suspend fun update(contact: Contact)

    suspend fun remove(contactId: String)
}

class SupabaseContactsRepository : ContactsRepository {

    private val supabase = SupabaseConfig.client

    override suspend fun getAll(): List<Contact> {

        val contacts = supabase
            .from("contact_details")
            .select()
            .decodeList<ContactEntity>()

        return contacts
            .filter { it.isActive }
            .map { it.toContact() }
    }

    override suspend fun add(contact: Contact) {

        supabase
            .from("contact_details")
            .insert(contact.toEntity())
    }

    override suspend fun update(contact: Contact) {

        if (contact.id.isBlank()) return

        supabase
            .from("contact_details")
            .update(contact.toEntity()) {

                filter {
                    eq("uuid", contact.id)
                }
            }
    }

    override suspend fun remove(contactId: String) {

        supabase
            .from("contact_details")
            .delete {

                filter {
                    eq("uuid", contactId)
                }
            }
    }
}