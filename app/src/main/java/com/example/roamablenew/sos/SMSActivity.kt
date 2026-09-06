package com.example.roamablenew.sos

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.location.Location
import android.net.Uri
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
// Imports necessary libraries for Android widgets
import androidx.compose.material3.Button
// Imports necessary libraries for 'MutableStateOf'
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
// Imports necessary libraries that send out SMS messages
import android.telephony.SmsManager
// Imports necessary libraries that check the build of this Android app
import android.os.Build
// Imports necessary libraries that shows a toast
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.TextButton
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.roamablenew.sos.model.EmergencyContact
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// Note: 'ComponentActivity()' is important for functions that are related to the 'Android' system.
//class SMSActivityClass : ComponentActivity() {
// Opts in for the experimental 'Material3API' for 'Scaffold' since the original 'Scaffold' will be deprecated in future
@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun SMSScreen(navController : NavHostController, userEmail : String, context : Context) {
    // Sets the variables for the text fields (widgets)
    var isLoading by remember {mutableStateOf(false)}
    var isSOSButtonPressed by remember {mutableStateOf(false)}
    var emergencyContacts by remember {mutableStateOf<List<EmergencyContact>>(listOf())}
    var userEmailState by remember {mutableStateOf(userEmail)}
    val scope = rememberCoroutineScope()
    // Sets the variables for the location services
    var fusedLocationClient : FusedLocationProviderClient
    val locationPermissionsState = rememberMultiplePermissionsState(
        listOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.SEND_SMS
        )
    )

    // Creates an instance of the Fused Location Provider Client
    fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    // Defines asynchronous functions
    suspend fun getEmergencyContacts() {
        isLoading = true
        withContext(Dispatchers.IO) {
            emergencyContacts = supabase.from("emergency_contacts")
                .select() { // columns = Columns.list("id", "contactName", "telephoneNum")
                    filter {
                        eq("userEmail", userEmailState)
                    }
                }.decodeList<EmergencyContact>()
        }
        isLoading = false
    }

    LaunchedEffect(Unit) {
        getEmergencyContacts()
    }

    if(!isLoading && isSOSButtonPressed) {
        // Gets the last known location
        // Checks for the permission with 'Google Anaylst'
        try {
            if(locationPermissionsState.allPermissionsGranted) {
                fusedLocationClient.lastLocation
                    .addOnSuccessListener { location : Location? ->
                        // Sends to the emergency contacts
                        emergencyContacts.forEach {
                                emergencyContact ->
                            sendSMSMessage(context, emergencyContact.telephoneNum, "HELP! I'M IN DANGER!!! Please save me at ${location?.latitude}, ${location?.longitude}. Thank you!")
                        }
                    }
            } else if(locationPermissionsState.shouldShowRationale) {   // If both location permissions have been denied
                //"We need your location permissions to know your location."
            } else {
                //"This feature requires location permissions."
            }
        } catch(e : SecurityException) {
            //
        }
    }

    Scaffold(
        /*topBar = {
            TopAppBar(
                // Sets the properties for 'TopAppBar'
                title = {
                    Text("Save Our Souls")
                }
            )
        },*/
        // Passes in the properties for 'Scaffold'
        modifier = Modifier.fillMaxSize()
    ) { innerPadding -> // Note: 'innerPadding' is essential for the 'Scaffold' widget.
        when(userEmailState) {
            "" ->
                Box(modifier = Modifier
                    .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Sorry, but you need to sign in for using this feature.",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            else ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .padding(16.dp)
                        .fillMaxHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally, // Note: 'Alignment' for horizontal.
                    verticalArrangement = Arrangement.Center    // Note: 'Arrangement' for vertical.
                ) {
                    Button(
                        onClick = {
                            // Sets 'isButtonPressed' to 'true'
                            isSOSButtonPressed = true

                            // Requests for permissions
                            locationPermissionsState.launchMultiplePermissionRequest()
                        },
                        enabled = !isLoading,
                        shape = CircleShape,  // Sets the shape of the button to a circle
                        colors = ButtonDefaults.buttonColors(Color.Red),
                        modifier = Modifier
                            .height(128.dp)
                            .width(128.dp)
                    ) {
                        // The widgets on the button
                        when {
                            isLoading ->
                                CircularProgressIndicator(
                                    color = Color.White
                                )
                            else ->
                                Text(
                                    text = "SOS",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 36.sp,    // Note: Use 'sp' for the size of the text.
                                    textAlign = TextAlign.Center
                                )
                        }
                    }

                    // Adds a space between the button and the text
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Press this button to send a SMS instantly to your emergency contacts",
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "if your feel your security is threatened.",
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(36.dp))

                    TextButton(
                        onClick = {
                            navController.navigate("EmergencyContactsActivity")
                        }
                    ) {
                        Text("View my emergency contacts →")
                    }
                }
        }
    }
}
// Sends a SMS message
// Note: This function must be moved onto 'onCreate'
fun sendSMSMessage(context : Context, phoneNumber : String, message : String) {
    // on the below line we are creating a try and catch block
    // Creates a new object
    try {
        // on below line we are initializing sms manager.
        //as after android 10 the getDefault function no longer works
        //so we have to check that if our android version is greater
        //than or equal toandroid version 6.0 i.e SDK 23
        val smsManager:SmsManager
        if (Build.VERSION.SDK_INT>=23) {
            //if SDK is greater that or equal to 23 then
            //this is how we will initialize the SmsManager
            smsManager = context.getSystemService(SmsManager::class.java)
        }
        else{
            //if user's SDK is less than 23 then
            //SmsManager will be initialized like this
            smsManager = SmsManager.getDefault()
        }

        // on below line we are sending text message.
        smsManager.sendTextMessage(phoneNumber, null, message, null, null)

        // on below line we are displaying a toast message for message send,
        Toast.makeText(context, "SOS sent.", Toast.LENGTH_LONG).show()
    } catch (e: Exception) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder
            .setTitle("ERROR")
            .setMessage(e.message.toString())

        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
}
@Preview(showBackground = true)
@Composable
fun Preview_SMSScreen() {
    SMSScreen(rememberNavController(), "", LocalContext.current)
}