package com.example.roamablenew.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

import com.example.roamablenew.data.Contact
import com.example.roamablenew.data.ContactCategory
import com.example.roamablenew.viewmodel.ContactsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelplineScreen(
    viewModel: ContactsViewModel = viewModel()
) {

    val contacts by viewModel.contacts.collectAsState()

    val errorMessage by
    viewModel.errorMessage.collectAsState()

    var editingContact by remember {
        mutableStateOf<Contact?>(null)
    }

    var isAddMode by remember {
        mutableStateOf(false)
    }

    var pendingDeleteContact by remember {
        mutableStateOf<Contact?>(null)
    }


    Scaffold(

        topBar = {

            TopAppBar(
                title = {
                    Text("Helpline Contacts")
                }
            )
        },

        floatingActionButton = {

            FloatingActionButton(

                onClick = {

                    isAddMode = true

                    editingContact = Contact(
                        name = "",
                        category =
                            ContactCategory.POLICE_STATION,
                        phoneNumber = ""
                    )
                }

            ) {

                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Contact"
                )
            }
        }

    ) { paddingValues ->


        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {


            // Error message

            if (errorMessage != null) {

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),

                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        Text(
                            text = errorMessage ?: "",
                            modifier =
                                Modifier.weight(1f)
                        )

                        TextButton(
                            onClick = {
                                viewModel.clearError()
                            }
                        ) {
                            Text("Dismiss")
                        }
                    }
                }
            }


            // No contacts

            if (contacts.isEmpty()) {

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {

                    Text(
                        "No emergency contacts found."
                    )
                }

            } else {


                // Contact list

                LazyColumn(

                    modifier =
                        Modifier.fillMaxSize(),

                    contentPadding =
                        PaddingValues(
                            bottom = 88.dp
                        )
                ) {


                    ContactCategory.entries
                        .forEach { category ->


                            val categoryContacts =
                                contacts.filter {
                                    it.category == category
                                }


                            if (categoryContacts.isNotEmpty()) {


                                // Category heading

                                item {

                                    Text(

                                        text =
                                            category.displayName,

                                        fontWeight =
                                            FontWeight.Bold,

                                        style =
                                            MaterialTheme
                                                .typography
                                                .titleMedium,

                                        modifier =
                                            Modifier.padding(
                                                16.dp,
                                                16.dp,
                                                16.dp,
                                                8.dp
                                            )
                                    )
                                }


                                // Contacts

                                items(
                                    categoryContacts,
                                    key = { it.id }
                                ) { contact ->


                                    ContactCard(

                                        contact = contact,

                                        onEditClick = {

                                            isAddMode = false

                                            editingContact =
                                                contact
                                        },

                                        onDeleteClick = {

                                            pendingDeleteContact =
                                                contact
                                        }
                                    )
                                }
                            }
                        }
                }
            }
        }
    }


    // Add/Edit dialog

    editingContact?.let { contact ->

        ContactEditDialog(

            contact = contact,

            isAddMode = isAddMode,

            onDismiss = {
                editingContact = null
            },

            onSave = { updatedContact ->

                if (isAddMode) {

                    viewModel.addContact(

                        name =
                            updatedContact.name,

                        category =
                            updatedContact.category,

                        phone =
                            updatedContact.phoneNumber,

                        address =
                            updatedContact.address,

                        notes =
                            updatedContact.notes
                    )

                } else {

                    viewModel.updateContact(
                        updatedContact
                    )
                }

                editingContact = null
            }
        )
    }


    // Delete confirmation

    pendingDeleteContact?.let { contact ->

        AlertDialog(

            onDismissRequest = {
                pendingDeleteContact = null
            },

            title = {
                Text("Remove Contact?")
            },

            text = {
                Text(
                    "Are you sure you want to remove " +
                            "\"${contact.name}\"?"
                )
            },

            confirmButton = {

                TextButton(

                    onClick = {

                        viewModel.removeContact(
                            contact.id
                        )

                        pendingDeleteContact = null
                    }

                ) {

                    Text("Remove")
                }
            },

            dismissButton = {

                TextButton(

                    onClick = {
                        pendingDeleteContact = null
                    }

                ) {

                    Text("Cancel")
                }
            }
        )
    }
}


@Composable
private fun ContactCard(

    contact: Contact,

    onEditClick: () -> Unit,

    onDeleteClick: () -> Unit

) {

    Card(

        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 16.dp,
                vertical = 4.dp
            )
    ) {

        Row(

            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),

            verticalAlignment =
                Alignment.CenterVertically

        ) {


            Column(
                modifier =
                    Modifier.weight(1f)
            ) {

                Text(
                    text = contact.name,
                    fontWeight =
                        FontWeight.SemiBold
                )


                Spacer(
                    modifier =
                        Modifier.height(4.dp)
                )


                Row(
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Icon(
                        imageVector =
                            Icons.Default.Phone,

                        contentDescription =
                            "Phone",

                        modifier =
                            Modifier.size(18.dp)
                    )

                    Spacer(
                        modifier =
                            Modifier.width(4.dp)
                    )

                    Text(
                        text =
                            contact.phoneNumber
                    )
                }


                if (contact.address.isNotBlank()) {

                    Spacer(
                        modifier =
                            Modifier.height(4.dp)
                    )

                    Text(
                        text =
                            contact.address,

                        style =
                            MaterialTheme
                                .typography
                                .bodySmall
                    )
                }


                if (contact.notes.isNotBlank()) {

                    Spacer(
                        modifier =
                            Modifier.height(4.dp)
                    )

                    Text(
                        text =
                            contact.notes,

                        style =
                            MaterialTheme
                                .typography
                                .bodySmall
                    )
                }
            }


            IconButton(
                onClick = onEditClick
            ) {

                Icon(
                    imageVector =
                        Icons.Default.Edit,

                    contentDescription =
                        "Edit Contact"
                )
            }


            IconButton(
                onClick = onDeleteClick
            ) {

                Icon(
                    imageVector =
                        Icons.Default.Delete,

                    contentDescription =
                        "Delete Contact"
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ContactEditDialog(

    contact: Contact,

    isAddMode: Boolean,

    onDismiss: () -> Unit,

    onSave: (Contact) -> Unit

) {

    var name by remember {
        mutableStateOf(contact.name)
    }

    var category by remember {
        mutableStateOf(contact.category)
    }

    var phone by remember {
        mutableStateOf(contact.phoneNumber)
    }

    var address by remember {
        mutableStateOf(contact.address)
    }

    var notes by remember {
        mutableStateOf(contact.notes)
    }

    var categoryExpanded by remember {
        mutableStateOf(false)
    }


    AlertDialog(

        onDismissRequest = onDismiss,

        title = {

            Text(
                if (isAddMode)
                    "Add Contact"
                else
                    "Edit Contact"
            )
        },

        text = {

            Column(
                verticalArrangement =
                    Arrangement.spacedBy(8.dp)
            ) {


                // Name

                OutlinedTextField(

                    value = name,

                    onValueChange = {
                        name = it
                    },

                    label = {
                        Text("Name")
                    },

                    singleLine = true,

                    modifier =
                        Modifier.fillMaxWidth()
                )


                // Category

                ExposedDropdownMenuBox(

                    expanded =
                        categoryExpanded,

                    onExpandedChange = {
                        categoryExpanded = it
                    }

                ) {

                    OutlinedTextField(

                        value =
                            category.displayName,

                        onValueChange = {},

                        readOnly = true,

                        label = {
                            Text("Category")
                        },

                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )


                    ExposedDropdownMenu(

                        expanded =
                            categoryExpanded,

                        onDismissRequest = {
                            categoryExpanded = false
                        }

                    ) {

                        ContactCategory.entries
                            .forEach { option ->

                                DropdownMenuItem(

                                    text = {
                                        Text(
                                            option.displayName
                                        )
                                    },

                                    onClick = {

                                        category = option

                                        categoryExpanded =
                                            false
                                    }
                                )
                            }
                    }
                }


                // Phone

                OutlinedTextField(

                    value = phone,

                    onValueChange = {
                        phone = it
                    },

                    label = {
                        Text("Phone Number")
                    },

                    singleLine = true,

                    modifier =
                        Modifier.fillMaxWidth()
                )


                // Address

                OutlinedTextField(

                    value = address,

                    onValueChange = {
                        address = it
                    },

                    label = {
                        Text("Address")
                    },

                    modifier =
                        Modifier.fillMaxWidth()
                )


                // Notes

                OutlinedTextField(

                    value = notes,

                    onValueChange = {
                        notes = it
                    },

                    label = {
                        Text("Notes")
                    },

                    modifier =
                        Modifier.fillMaxWidth()
                )
            }
        },

        confirmButton = {

            TextButton(

                onClick = {

                    onSave(

                        contact.copy(

                            name =
                                name.trim(),

                            category =
                                category,

                            phoneNumber =
                                phone.trim(),

                            address =
                                address.trim(),

                            notes =
                                notes.trim()
                        )
                    )
                },

                enabled =
                    name.isNotBlank() &&
                            phone.isNotBlank()

            ) {

                Text(
                    if (isAddMode)
                        "Add"
                    else
                        "Save"
                )
            }
        },

        dismissButton = {

            TextButton(
                onClick = onDismiss
            ) {

                Text("Cancel")
            }
        }
    )
}