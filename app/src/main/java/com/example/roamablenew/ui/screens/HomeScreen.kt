package com.example.roamablenew.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.roamablenew.data.Location
import com.example.roamablenew.data.LocationRepository
import com.example.roamablenew.navigation.Destination
import com.example.roamablenew.viewmodel.UserViewModel
import com.example.roamablenew.ui.screens.MapMarker
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import kotlin.collections.emptyList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainMenu() {
    val navController = rememberNavController()
    val userViewModel: UserViewModel = viewModel()

    Scaffold(
        bottomBar = { BottomNavBar(navController = navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Destination.MAP.route,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            composable(Destination.MAP.route) { MapScreen() }
            composable(Destination.HELPLINE.route) { HelplineScreen() }
            composable(Destination.SOS.route) { SosScreen() }
            composable(Destination.PROFILE.route) { ProfileScreen(navController, userViewModel) }
            composable(Destination.LOGIN.route) { LoginScreen(navController, userViewModel) }
            composable(Destination.REGISTER.route) { RegisterScreen(navController, userViewModel) }
        }
    }
}

@Composable
fun MapScreen() {
    val usernameState = rememberTextFieldState(initialText = "")
    val fruits = listOf("Apple", "Banana", "Cherry")
    var mapView by remember { mutableStateOf<MapView?>(null) }

    val malaysiaCenter = remember { GeoPoint(4.2105, 101.9758) }
    val malaysiaZoom = 6.0

    val repository = remember { LocationRepository() }
    var locations by remember { mutableStateOf<List<Location>>(emptyList()) }

    LaunchedEffect(Unit) {
        try {
            locations = repository.getAllLocations()
        } catch (e: Exception) {
            Log.e("MapScreen", "Failed to load locations", e)
        }
    }

    val markers = remember(locations) {
        locations
            .filter { it.latitude != null && it.longitude != null }
            .map { MapMarker(it.name, it.latitude!!, it.longitude!!) }
    }


    Box(modifier = Modifier.fillMaxSize()) {
        OsmMapView(
            markers = markers,
            onMapReady = { mapView = it }
        )

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SimpleSearchBar(usernameState, { query -> }, fruits, false, {})
            Spacer(modifier = Modifier.height(16.dp))
            TagsRow()
        }

        FloatingActionButton(
            onClick = {
                mapView?.controller?.setZoom(malaysiaZoom)
                mapView?.controller?.animateTo(malaysiaCenter)
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Default.MyLocation, contentDescription = "Recenter to Malaysia")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleSearchBar(
    textFieldState: TextFieldState,
    onSearch: (String) -> Unit,
    searchResults: List<String>,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {

    Box(
        modifier
            //.fillMaxSize()
            .semantics { isTraversalGroup = true }
    ) {
        SearchBar(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .semantics { traversalIndex = 0f },
            inputField = {
                SearchBarDefaults.InputField(
                    query = textFieldState.text.toString(),
                    onQueryChange = { textFieldState.edit { replace(0, length, it) } },
                    onSearch = {
                        onSearch(textFieldState.text.toString())
                        onExpandedChange(false)
                    },
                    expanded = expanded,
                    onExpandedChange = onExpandedChange,
                    placeholder = { Text("Search") }
                )
            },
            expanded = expanded,
            onExpandedChange = onExpandedChange,
        ) {
            // Display search results in a scrollable column
            Column(Modifier.verticalScroll(rememberScrollState())) {
                searchResults.forEach { result ->
                    ListItem(
                        headlineContent = { Text(result) },
                        modifier = Modifier
                            .clickable {
                                textFieldState.edit { replace(0, length, result) }
                                onExpandedChange(false)
                            }
                            .fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
fun TagsRow() {
    val widgetList = listOf("Widget 1", "Widget 2", "Widget 3", "Widget 4", "Widget 5", "Widget 6", "Widget 7")

    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(widgetList) { title ->
            WidgetCard(title = title)
        }
    }
}

@Composable
fun WidgetCard(title: String) {
    Card(
        modifier = Modifier
            .width(60.dp)
            .height(30.dp)
    ) {
        Text(
            text = title,
            modifier = Modifier.padding(horizontal = 0.dp, vertical = 5.dp)
        )
    }
}

@Composable
fun BottomNavBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Define which destinations should be in the bottom bar
    val bottomTabs = listOf(Destination.MAP, Destination.HELPLINE, Destination.SOS, Destination.PROFILE)

    NavigationBar {
        bottomTabs.forEach { destination ->
            if (destination == Destination.SOS) {
                // Oversized, styled SOS item
                NavigationBarItem(
                    selected = currentRoute == destination.route,
                    onClick = {
                        navController.navigate(destination.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    icon = {
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .clip(CircleShape)
                                .background(Color.Red),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = destination.icon,
                                contentDescription = destination.contentDescription,
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    },
                    label = { Text(destination.label) }
                )
            } else {
                NavigationBarItem(
                    selected = currentRoute == destination.route,
                    onClick = {
                        navController.navigate(destination.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    icon = {
                        Icon(
                            imageVector = destination.icon,
                            contentDescription = destination.contentDescription
                        )
                    },
                    label = { Text(destination.label) }
                )
            }
        }
    }
}

@Composable
fun SosScreen() {
    Text("Coming Soon")
}

@Composable
fun HelplineScreen() {
    Text("Coming Soon")
}
