package com.example.roamablenew.viewmodel

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.roamablenew.data.Location
import com.example.roamablenew.data.SupabaseConfig
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.launch

class LocationViewModel : ViewModel() {
    private val _locations = mutableStateOf<List<Location>>(emptyList())
    val locations: State<List<Location>> = _locations

    fun addLocation(location: Location, onResult: (Boolean) -> Unit = {}) {
        viewModelScope.launch {
            try {
                SupabaseConfig.client.from("locations").insert(location)
                fetchLocations() // Refresh list
                onResult(true)
            } catch (e: Exception) {
                Log.e("LocationViewModel", "Add Error: ${e.message}")
                onResult(false)
            }
        }
    }

    fun fetchLocations() {
        viewModelScope.launch {
            try {
                val list = SupabaseConfig.client.from("locations")
                    .select(columns = Columns.ALL)
                    .decodeList<Location>()
                _locations.value = list
            } catch (e: Exception) {
                Log.e("LocationViewModel", "Fetch Error: ${e.message}")
            }
        }
    }

    // UPDATE
    fun updateLocation(location: Location) {
        viewModelScope.launch {
            try {
                SupabaseConfig.client.from("locations").update(location) {
                    filter { eq("id", location.id ?: "") }
                }
                fetchLocations()
            } catch (e: Exception) {
                Log.e("LocationViewModel", "Update Error: ${e.message}")
            }
        }
    }

    fun deleteLocation(locationId: String) {
        viewModelScope.launch {
            try {
                SupabaseConfig.client.from("locations").delete {
                    filter { eq("id", locationId) }
                }
                fetchLocations()
            } catch (e: Exception) {
                Log.e("LocationViewModel", "Delete Error: ${e.message}")
            }
        }
    }
}