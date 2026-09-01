package com.example.roamablenew.viewmodel

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.roamablenew.data.SupabaseConfig
import com.example.roamablenew.data.User
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.launch

class UserViewModel : ViewModel() {
    private val _currentUser = mutableStateOf<User?>(null)
    val currentUser: State<User?> = _currentUser

    fun register(user: User, onResult: (Boolean) -> Unit = {}) {
        viewModelScope.launch {
            try {
                SupabaseConfig.client.from("users").insert(user)
                _currentUser.value = user
                onResult(true)
            } catch (e: Exception) {
                Log.e("UserViewModel", "Register Error: ${e.message}")
                onResult(false)
            }
        }
    }

    fun login(email: String, password: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                val response = SupabaseConfig.client.from("users")
                    .select(columns = Columns.ALL) {
                        filter {
                            eq("email", email)
                            eq("password", password)
                        }
                    }.decodeSingleOrNull<User>()

                if (response != null) {
                    _currentUser.value = response
                    onResult(true)
                } else {
                    Log.w("UserViewModel", "Login Failed: User not found")
                    onResult(false)
                }
            } catch (e: Exception) {
                Log.e("UserViewModel", "Login Error: ${e.message}")
                e.printStackTrace()
                onResult(false)
            }
        }
    }

    fun updateProfile(updatedUser: User) {
        viewModelScope.launch {
            try {
                SupabaseConfig.client.from("users").update(updatedUser) {
                    filter { eq("email", updatedUser.email ?: "") }
                }
                _currentUser.value = updatedUser
            } catch (e: Exception) {
                Log.e("UserViewModel", "Update Error: ${e.message}")
            }
        }
    }

    fun logout() {
        _currentUser.value = null
    }

    fun deleteProfile() {
        val userToDelete = _currentUser.value
        if (userToDelete != null) {
            viewModelScope.launch {
                try {
                    SupabaseConfig.client.from("users").delete {
                        filter { eq("email", userToDelete.email ?: "") }
                    }
                    _currentUser.value = null
                } catch (e: Exception) {
                    Log.e("UserViewModel", "Delete Error: ${e.message}")
                }
            }
        }
    }
}
