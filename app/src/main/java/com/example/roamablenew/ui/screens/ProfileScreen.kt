package com.example.roamablenew.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.roamablenew.data.User
import com.example.roamablenew.navigation.Destination
import com.example.roamablenew.viewmodel.UserViewModel

@Composable
fun ProfileScreen(navController: NavController, viewModel: UserViewModel) {
    val currentUser by viewModel.currentUser
    var isEditing by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    if (currentUser == null) {
        GuestProfileScreen(navController)
    } else {
        if (isEditing) {
            EditProfileScreen(
                user = currentUser!!,
                onSave = { updatedUser ->
                    viewModel.updateProfile(updatedUser)
                    isEditing = false
                },
                onCancel = { isEditing = false }
            )
        } else if (showDeleteConfirm) {
            Column(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("Confirm Delete Profile?", fontSize = 20.sp)
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = {
                        viewModel.deleteProfile()
                        showDeleteConfirm = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Confirm Delete")
                }
                Spacer(modifier = Modifier.height(8.dp))
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("Go Back")
                }
            }
        } else {
            ViewProfileScreen(
                user = currentUser!!,
                onEdit = { isEditing = true },
                onLogout = { viewModel.logout() },
                onDelete = { showDeleteConfirm = true }
            )
        }
    }
}

@Composable
fun GuestProfileScreen(navController: NavController) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.AccountCircle,
            contentDescription = null,
            modifier = Modifier.size(100.dp)
        )
        Text(text = "Guest User", fontSize = 24.sp)
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = { navController.navigate(Destination.LOGIN.route) }) {
            Text("Log In")
        }
    }
}

@Composable
fun ViewProfileScreen(
    user: User,
    onEdit: () -> Unit,
    onLogout: () -> Unit,
    onDelete: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.AccountCircle,
            contentDescription = null,
            modifier = Modifier.size(100.dp)
        )
        Text(text = user.name ?: "Unknown", fontSize = 24.sp)
        
        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
        
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(text = "Email: ${user.email ?: "N/A"}", fontSize = 18.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Disability Type: ${user.disability ?: "N/A"}", fontSize = 18.sp)
            Spacer(modifier = Modifier.height(8.dp))
            val displayAddress = if (user.address.isNullOrEmpty()) "No Set" else user.address
            Text(text = "Home Address: $displayAddress", fontSize = 18.sp)
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .clickable { onEdit() },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Edit, contentDescription = null)
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = "Edit Profile", fontSize = 20.sp)
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .clickable { onLogout() },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                contentDescription = null,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = "Log Out", fontSize = 20.sp)
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onDelete,
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Delete, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Delete Profile")
        }
    }
}

@Composable
fun EditProfileScreen(
    user: User,
    onSave: (User) -> Unit,
    onCancel: () -> Unit
) {
    var name by remember { mutableStateOf(user.name ?: "") }
    var disability by remember { mutableStateOf(user.disability ?: "") }
    var address by remember { mutableStateOf(user.address ?: "") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Edit Profile", fontSize = 24.sp)
        Spacer(modifier = Modifier.height(16.dp))
        
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("User Name") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = disability,
            onValueChange = { disability = it },
            label = { Text("Disability Type") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = address,
            onValueChange = { address = it },
            label = { Text("Home Address") },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            Button(onClick = onCancel, colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)) {
                Text("Cancel")
            }
            Button(onClick = {
                val updated = User(name, user.email, user.password, disability, address)
                onSave(updated)
            }) {
                Text("Save")
            }
        }
    }
}
