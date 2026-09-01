package com.example.roamablenew.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.roamablenew.data.User

class UserViewModel : ViewModel() {
    private val _currentUser = mutableStateOf<User?>(null)
    val currentUser: State<User?> = _currentUser

    private val _registeredUsers = mutableStateListOf<User>()

    fun register(user: User) {
        _registeredUsers.add(user)
        _currentUser.value = user
    }

    fun login(email: String, password: String): Boolean {
        for (user in _registeredUsers) {
            if (user.email == email && user.password == password) {
                _currentUser.value = user
                return true
            }
        }
        return false
    }

    fun logout() {
        _currentUser.value = null
    }

    fun updateProfile(updatedUser: User) {
        for (i in 0 until _registeredUsers.size) {
            if (_registeredUsers[i].email == updatedUser.email) {
                _registeredUsers[i] = updatedUser
                _currentUser.value = updatedUser
                break
            }
        }
    }

    fun deleteProfile() {
        val userToDelete = _currentUser.value
        if (userToDelete != null) {
            _registeredUsers.remove(userToDelete)
            _currentUser.value = null
        }
    }
}
