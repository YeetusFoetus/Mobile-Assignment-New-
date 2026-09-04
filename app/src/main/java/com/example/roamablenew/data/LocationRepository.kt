package com.example.roamablenew.data

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns

class LocationRepository {
    private val db = SupabaseConfig.client.postgrest

    suspend fun getAllLocations(): List<Location> =
        db.from("locations").select().decodeList()

    suspend fun getTagsForLocation(locationId: String): List<AccessibilityTag> =
        db.from("accessibility_tags")
            .select { filter { eq("location_id", locationId) } }
            .decodeList()

    suspend fun addTag(locationId: String, tagType: String, userEmail: String?) {
        db.from("accessibility_tags").insert(
            AccessibilityTag(locationId = locationId, tagType = tagType, userId = userEmail)
        )
    }
}