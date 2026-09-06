package com.example.roamablenew.sos

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
//import com.example.romable_sms.entities.EmergencyContact
import com.example.roamablenew.sos.model.EmergencyContact
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// Initialises the 'Supabase' client with the 'supabaseUrl' and 'supabaseKey' of the project.
val supabase = createSupabaseClient(
    supabaseUrl = "https://pqnnvnukefaeqajbbfpz.supabase.co",
    supabaseKey = "sb_publishable_WJVRwTzCNbeDVdUOoMf8oQ_p8mrTHYo"
) {
    install(Postgrest)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmergencyContactsScreen(navController : NavHostController, userEmail : String) {
    // Initialises mutable states
    var emergencyContacts by remember { mutableStateOf<List<EmergencyContact>>(listOf()) }
    var isLoading by remember{mutableStateOf(false)}
    var isUpdated by remember {mutableStateOf(false)}
    var userEmailState by remember {mutableStateOf(userEmail)}
    var snackbarHostState by remember { mutableStateOf(SnackbarHostState()) }
    val scope = rememberCoroutineScope()    // used to pass to another relevant composable functions

    // Retrieves the emergency contacts from the remote database
    suspend fun fetchEmergencyContacts(userEmail : String) {
        // Shows the loading circle to indicate that the list of emergency contacts is being fetched
        isLoading = true
        // Retrieves the list of the emergency contacts based on the user's email address
        withContext(Dispatchers.IO) {
            emergencyContacts = supabase.from("emergency_contacts")
                .select() { // columns = Columns.list("id", "contactName", "telephoneNum")
                    filter {
                        eq("userEmail", userEmail)
                    }
                }.decodeList<EmergencyContact>()
        }
        // Hides the loading circle after fetching the list of emergency contacts
        isLoading = false
    }

    // Defines the user interface
    fun refresh() {
        scope.launch {
            fetchEmergencyContacts(userEmail)
        }
    }

    // Retrieves the list of the emergency contacts for the first time
    LaunchedEffect(Unit) {
        fetchEmergencyContacts(userEmail)
    }

    // Designs the graphical user interface (GUI)
    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        modifier = Modifier.fillMaxWidth(),
        topBar = {
            MediumTopAppBar(
                modifier = Modifier.fillMaxWidth(),
                title = {Text("Emergency contacts")},
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
        },
        floatingActionButton = {floatingButton_add(navController)}   // Adds a floating button to the graphical user interface (GUI)
    ) {
        innerPadding ->
        when {
            userEmailState == "" ->
                Box(modifier = Modifier
                    .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Sorry, but you need to sign in to continue.",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            // When 'isLoading' is true
            isLoading -> Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
            // When the emergency contacts is empty
            emergencyContacts.isEmpty() ->
                Box(modifier = Modifier
                    .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No emergency contacts found!",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            else ->
            // Shows the lazy column
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(innerPadding)
                    .padding(16.dp)
            ) {
                items(
                    emergencyContacts,
                    key = { emergencyContact -> emergencyContact.id }
                ) {
                    emergencyContact ->
                    isUpdated = LazyColumnItem(
                        navController = navController,
                        contact = emergencyContact,
                        userEmail = userEmail,
                        scope = scope,
                        snackbarHostState = snackbarHostState
                    )
                    when {
                        isUpdated -> refresh()
                    }
                }
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LazyColumnItem(navController: NavHostController, scope : CoroutineScope, contact : EmergencyContact, userEmail : String, snackbarHostState: SnackbarHostState) : Boolean {
    // Initialises mutable state(s)
    var isDropdownMenuExpanded by remember {mutableStateOf(false)}
    var isDeleting by remember {mutableStateOf(false)}
    var deletingContact : EmergencyContact? by remember {mutableStateOf(null)}
    var isRefreshed by remember {mutableStateOf(false)}

    // Defines functions
    fun showMessage(message : String) {
        scope.launch { snackbarHostState.showSnackbar(message) }
    }

    // Shows an alert dialog when an emergency contact is being deleted
    when {
        isDeleting ->
            showDialog(
                contact = deletingContact,
                onConfirm = {
                    scope.launch {
                        try {
                            deleteEmergencyContact(id = deletingContact?.id ?: 0, userEmail = userEmail)
                            isRefreshed = true
                        } catch(e : Exception) {
                            showMessage(e.message ?: "Sorry, but we have encountered a problem. Please try again")
                        } finally{
                            deletingContact = null
                            isDeleting = false
                        }
                    }
                },
                onDismiss = {
                    // Unassigns the 'deletingContact' variable
                    deletingContact = null
                    // Hides the dialog box by setting 'isDeleting' to 'false'
                    isDeleting = false
                }
            )
        else ->
            deletingContact = null
    }
    // Designs the widget
    ListItem(
        modifier = Modifier.fillMaxWidth(),
        leadingContent = {
            Icon(
                painter = painterResource(
                    id = R.drawable.outline_account_circle_24
                ),
                contentDescription = "User"
            )
        },
        headlineContent = {
            // The name of the contact
            Text(
                text = contact.contactName,
                fontSize = MaterialTheme.typography.titleMedium.fontSize
            )
        },
        supportingContent = {
            // The telephone number of the contact
            Text(
                text = contact.telephoneNum
            )
        },
        trailingContent = {
            ExposedDropdownMenuBox(
                expanded = isDropdownMenuExpanded,
                onExpandedChange = {isDropdownMenuExpanded = it}
            ) {
                IconButton(
                    onClick = {},
                    modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_more_vert_24),
                        contentDescription = "More options"
                    )
                }
                DropdownMenu(
                    expanded = isDropdownMenuExpanded,
                    onDismissRequest = {isDropdownMenuExpanded = false},
                ) {
                    DropdownMenuItem(
                        leadingIcon = {
                            Icon(
                                painter = painterResource(R.drawable.outline_edit_24),
                                contentDescription = "Edit"
                            )
                        },
                        text = {Text("Edit")},
                        onClick = {
                            navController.navigate("EmergencyContactForm/${contact.id}/${contact.contactName}/${contact.telephoneNum}")
                        }
                    )
                    DropdownMenuItem(
                        leadingIcon = {
                            Icon(
                                painter = painterResource(R.drawable.outline_delete_24),
                                contentDescription = "Delete"
                            )
                        },
                        text = {Text("Delete")},
                        onClick = {
                            // Specifies the emergency contact that is being deleted
                            deletingContact = contact
                            isDeleting = true
                        }
                    )
                }
            }
        }
    )
    return isRefreshed
}
@Composable
fun floatingButton_add(navController : NavHostController) {
    FloatingActionButton(
        onClick = {
            navController.navigate("EmergencyContactForm/0//")
        },
    ) {
        Icon(painter = painterResource(id = R.drawable.outline_add_24),
            contentDescription = "Add a new contact")
    }
}
@Composable
fun showDialog(contact : EmergencyContact?, onConfirm : () -> Unit, onDismiss : () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Deleting '${contact?.contactName}'?") },
        text = { Text("He/she won't receive your SMS message if you do so.") },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("Delete")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss, colors = ButtonDefaults.textButtonColors()) {
                Text("Cancel")
            }
        }
    )
}
// Deletes an emergency contact
suspend fun deleteEmergencyContact(id : Int, userEmail : String) : Boolean {
    // Initialises necessary variables
    var isDeleted = false
    withContext(Dispatchers.IO) {
        try {
            supabase.from("emergency_contacts").delete {
                filter {
                    eq("id", id)
                    eq("userEmail", userEmail)
                }
            }
            isDeleted = true
        } catch (e : Exception) {
            isDeleted = false
        }
    }
    return isDeleted
}
@Preview(showBackground = true)
@Composable
private fun Preview_emergencyContactsScreen() {
    // Shows a preview of the screen
    EmergencyContactsScreen(navController = rememberNavController(), "mmelvis627@outlook.com")
}