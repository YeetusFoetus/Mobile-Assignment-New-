package com.example.roamablenew.viewmodel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.roamablenew.data.SupabaseConfig
import com.example.roamablenew.data.User
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.launch

// Changed to AndroidViewModel to access Context for SharedPreferences
class UserViewModel(application: Application) : AndroidViewModel(application) {
    private val prefs = application.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    
    private val _currentUser = mutableStateOf<User?>(null)
    val currentUser: State<User?> = _currentUser

    init {
        checkAutoLogin()
    }

    private fun checkAutoLogin() {
        val savedEmail = prefs.getString("logged_in_email", null)
        if (savedEmail != null) {
            viewModelScope.launch {
                try {
                    val user = SupabaseConfig.client.from("users")
                        .select {
                            filter { eq("email", savedEmail) }
                        }.decodeSingleOrNull<User>()
                    
                    if (user != null) {
                        _currentUser.value = user
                        Log.d("UserViewModel", "Auto login successful: $savedEmail")
                    }
                } catch (e: Exception) {
                    Log.e("UserViewModel", "Auto login failed: ${e.message}")
                }
            }
        }
    }

    fun register(user: User, onResult: (Boolean) -> Unit = {}) {
        viewModelScope.launch {
            try {
                SupabaseConfig.client.from("users").insert(user)
                _currentUser.value = user
                
                // Save login state locally
                prefs.edit().putString("logged_in_email", user.email).apply()
                
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
                    
                    // Save login state locally
                    prefs.edit().putString("logged_in_email", email).apply()
                    
                    onResult(true)
                } else {
                    onResult(false)
                }
            } catch (e: Exception) {
                Log.e("UserViewModel", "Login Error: ${e.message}")
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
        // Clear saved session
        prefs.edit().remove("logged_in_email").apply()
    }

    fun deleteProfile() {
        val userToDelete = _currentUser.value
        if (userToDelete != null) {
            viewModelScope.launch {
                try {
                    SupabaseConfig.client.from("users").delete {
                        filter { eq("email", userToDelete.email ?: "") }
                    }
                    logout() // Trigger logout logic to clear local storage
                } catch (e: Exception) {
                    Log.e("UserViewModel", "Delete Error: ${e.message}")
                }
            }
        }
    }

    fun getEmail() : String {
        return currentUser.value?.email ?: ""
    }
}
