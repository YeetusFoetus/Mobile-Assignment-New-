package com.example.roamablenew.data

import io.github.jan.supabase.postgrest.postgrest

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

    suspend fun removeTag(locationId: String, tagType: String, userEmail: String) {
        db.from("accessibility_tags").delete {
            filter {
                eq("location_id", locationId)
                eq("tag_type", tagType)
                eq("user_id", userEmail)
            }
        }
    }

    suspend fun getAllTags(): List<AccessibilityTag> =
        db.from("accessibility_tags").select().decodeList()
}