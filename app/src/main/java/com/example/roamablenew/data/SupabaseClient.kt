package com.example.roamablenew.data

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest

object SupabaseConfig {
    val client = createSupabaseClient(
        supabaseUrl = "https://pqnnvnukefaeqajbbfpz.supabase.co",
        supabaseKey = "sb_publishable_WJVRwTzCNbeDVdUOoMf8oQ_p8mrTHYo"
    ) {
        install(Postgrest)
    }
}
