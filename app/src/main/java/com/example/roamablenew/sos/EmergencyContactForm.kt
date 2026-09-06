package com.example.roamablenew.sos

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.roamablenew.R
import com.example.roamablenew.sos.model.userInputs.EmergencyContact
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmergencyContactForm(navController : NavHostController,
                         id : Int = 0, contactName : String = "", telephoneNum : String = "", userEmail : String = "") {
    // Connects to Supabase
    val supabase = createSupabaseClient(
        supabaseUrl = "https://pqnnvnukefaeqajbbfpz.supabase.co",
        supabaseKey = "sb_publishable_WJVRwTzCNbeDVdUOoMf8oQ_p8mrTHYo"
    ) {
        install(Postgrest)
    }

    // Initialises 'CoroutineScope'
    val scope = rememberCoroutineScope()

    // Initialises necessary variables
    var id_state by remember { mutableStateOf(id) }
    var contactName_state by remember { mutableStateOf(contactName) }    // ?: stands for 'elvis operator'
    var telephoneNum_state by remember { mutableStateOf(telephoneNum)}
    var userEmail_state by remember { mutableStateOf(userEmail) }

    // Initialises variables for widgets
    val snackbarHostState by remember { mutableStateOf(SnackbarHostState()) }
    var isLoading by remember { mutableStateOf(false) }

    //
    //var error by remember { mutableStateOf("") }

    // Defines functions
    fun showMessage(message : String) {
        scope.launch { snackbarHostState.showSnackbar(message) }
    }

    // Defines asynchronous functions
    suspend fun submit() : Boolean {    // Returns 'true' if it is a success
        // Shows the loading circle
        isLoading = true
        try {
            withContext(Dispatchers.IO) {
                if(id_state == 0) {   // If the user is adding a new emergency contact
                    supabase.from("emergency_contacts").insert(
                        EmergencyContact(
                            contactName = contactName_state,
                            telephoneNum = telephoneNum_state,
                            userEmail = userEmail_state
                        )
                    )
                } else {
                    supabase.from("emergency_contacts").update(
                        {
                            set("contactName", contactName_state)
                            set("telephoneNum", telephoneNum_state)
                        }
                    ) {
                        filter {
                            eq("id", id_state)
                            eq("userEmail", userEmail)
                        }
                    }
                }
            }
            return true
        } catch(e: Exception) {
            showMessage(e.message ?: "Sorry, but we have encountered a problem. Please try again.")
        } finally {
            isLoading = false
        }
        return false
    }

    Scaffold(
        snackbarHost = {SnackbarHost(hostState = snackbarHostState)},
        modifier = Modifier.fillMaxWidth(),
        topBar = {
            TopAppBar(
                modifier = Modifier.fillMaxWidth(),
                title = {
                    when {
                        (id == 0) -> Text("Add an emergency contact")
                        else -> Text("Edit an emergency contact")
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            // Returns to the previous screen
                            navController.popBackStack()
                        }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.outline_arrow_left_alt_24),
                            contentDescription = "Go back to the previous screen"
                        )
                    }
                }
            )
        }
    ) {
        innerPadding ->
        when {
            userEmail_state == "" ->
                Box(modifier = Modifier
                    .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Sorry, but you need to sign in to continue.",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            isLoading ->
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            else ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(16.dp)
                ) {
                    // The fields for the contact form
                    TextField(
                        value = contactName_state,
                        onValueChange = {contactName_state = it},
                        modifier = Modifier.fillMaxWidth(),
                        label = {Text("Contact's name")}
                    )
                    TextField(
                        value = telephoneNum_state,
                        onValueChange = {telephoneNum_state = it},
                        modifier = Modifier.fillMaxWidth(),
                        label = {Text("Telephone number")},
                        supportingText = {Text("e.g.: 60123456789")},
                        leadingIcon = {Icon(painter = painterResource(R.drawable.outline_add_24), contentDescription = "Plus sign")}
                    )
                    //Text(error)
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            // Performs data validation
                            if(contactName_state.isEmpty()) {
                                showMessage("Sorry, but the contact's name cannot be empty!")
                            } else if(telephoneNum_state.isEmpty()) {
                                showMessage("Sorry, but the telephone number cannot be empty!")
                            // Checks whether the telephone number follows an appropriate format
                            } else if(telephoneNum_state.contains('+')) {
                                showMessage("Sorry, but you do not need to include a 'plus' sign in your telephone number. :)")
                            } else if(telephoneNum_state.toLongOrNull() == null) {  // Note: Use 'long' for integers that have more than nine (9) digits.
                                showMessage("Sorry, but your telephone number should consist of digits only.")
                            } else {
                                // Submit the data
                                scope.launch {
                                    if(submit()) {
                                        // Displays an appropriate confirmation message
                                        if(id_state == 0)
                                            showMessage("The emergency contact has been added successfully.")
                                        else
                                            showMessage("The emergency contact has been edited successfully.")
                                        // Goes to the previous screen
                                        navController.popBackStack()
                                    }
                                }
                            }
                        }
                    ) {
                        Text("Save")
                    }
                }
        }
    }
}
// For previewing
@Preview(showBackground = true)
@Composable
fun Preview_EmergencyContactForm() {
    EmergencyContactForm(navController = rememberNavController(), 0)
}