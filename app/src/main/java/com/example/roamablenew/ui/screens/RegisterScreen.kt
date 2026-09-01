package com.example.roamablenew.ui.screens

import androidx.compose.foundation.layout.*
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
fun RegisterScreen(navController: NavController, viewModel: UserViewModel) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var disability by remember { mutableStateOf("") }
    var nameError by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf(false) }
    var disabilityError by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Create Profile", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        
        OutlinedTextField(
            value = name,
            onValueChange = { 
                name = it
                nameError = false
            },
            label = { Text("User Name") },
            modifier = Modifier.fillMaxWidth(),
            isError = nameError
        )
        if (nameError) {
            Text("User name is required", color = Color.Red, fontSize = 12.sp, modifier = Modifier.align(Alignment.Start).padding(start = 8.dp))
        }

        Spacer(modifier = Modifier.height(8.dp))
        
        OutlinedTextField(
            value = email,
            onValueChange = { 
                email = it
                emailError = false
            },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            isError = emailError
        )
        if (emailError) {
            Text("Email is required", color = Color.Red, fontSize = 12.sp, modifier = Modifier.align(Alignment.Start).padding(start = 8.dp))
        }

        Spacer(modifier = Modifier.height(8.dp))
        
        OutlinedTextField(
            value = password,
            onValueChange = { 
                password = it
                passwordError = false
            },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            isError = passwordError
        )
        if (passwordError) {
            Text("Password is required", color = Color.Red, fontSize = 12.sp, modifier = Modifier.align(Alignment.Start).padding(start = 8.dp))
        }

        Spacer(modifier = Modifier.height(8.dp))
        
        OutlinedTextField(
            value = disability,
            onValueChange = { 
                disability = it
                disabilityError = false
            },
            label = { Text("Disability Type(Example: Visual Impairment)") },
            modifier = Modifier.fillMaxWidth(),
            isError = disabilityError
        )
        if (disabilityError) {
            Text("Disability type is required", color = Color.Red, fontSize = 12.sp, modifier = Modifier.align(Alignment.Start).padding(start = 8.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                nameError = name.isEmpty()
                emailError = email.isEmpty()
                passwordError = password.isEmpty()
                disabilityError = disability.isEmpty()

                if (!nameError && !emailError && !passwordError && !disabilityError) {
                    val newUser = User(name, email, password, disability)
                    viewModel.register(newUser) { success ->
                        if (success) {
                            navController.navigate(Destination.PROFILE.route)
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Sign Up")
        }
    }
}
