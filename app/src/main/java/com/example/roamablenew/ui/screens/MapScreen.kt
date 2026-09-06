package com.example.roamablenew.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.roamablenew.data.AccessibilityTag
import com.example.roamablenew.data.Location
import com.example.roamablenew.data.LocationRepository
import com.example.roamablenew.ui.map.AddAccessibilityInfoSheet
import com.example.roamablenew.ui.map.LocationDetailSheet
import com.example.roamablenew.ui.map.OsmMapView
import com.example.roamablenew.ui.map.SheetMode
import com.example.roamablenew.viewmodel.UserViewModel
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(userViewModel: UserViewModel) {
    var locations by remember { mutableStateOf<List<Location>>(emptyList()) }
    var mapView by remember { mutableStateOf<MapView?>(null) }
    var selectedLocation by remember { mutableStateOf<Location?>(null) }
    var tags by remember { mutableStateOf<List<AccessibilityTag>>(emptyList()) }
    var sheetMode by remember { mutableStateOf(SheetMode.NONE) }
    var allTags by remember { mutableStateOf<List<AccessibilityTag>>(emptyList()) }
    var selectedTagFilters by remember { mutableStateOf<Set<String>>(emptySet()) }
    var expanded by rememberSaveable { mutableStateOf(false) }

    val usernameState = rememberTextFieldState(initialText = "")
    val malaysiaCenter = remember { GeoPoint(4.2105, 101.9758) }
    val malaysiaZoom = 6.0
    val repository = remember { LocationRepository() }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val currentUserEmail = userViewModel.currentUser.value?.email ?: ""
    val query = usernameState.text.toString()
    val searchResults = remember(query, locations) {
        if (query.isBlank()) emptyList()
        else locations.filter {
            it.name.contains(query, ignoreCase = true) ||
                    (it.address?.contains(query, ignoreCase = true) == true)
        }.map { it.name }
    }
    val filteredLocations = remember(locations, allTags, selectedTagFilters) {
        if (selectedTagFilters.isEmpty()) locations
        else {
            val matchingIds = allTags.filter { it.tagType in selectedTagFilters }.map { it.locationId }.toSet()
            locations.filter { it.id in matchingIds }
        }
    }

    LaunchedEffect(Unit) {
        try {
            locations = repository.getAllLocations()
        } catch (e: Exception) {
            Log.e("MapScreen", "Failed to load locations", e)
        }
    }

    fun refreshTags(locationId: String) {
        scope.launch {
            tags = try { repository.getTagsForLocation(locationId) }
            catch (e: Exception) { Log.e("MapScreen", "Failed to load tags", e); tags}
        }
    }

    LaunchedEffect(selectedLocation) {
        selectedLocation?.let { refreshTags(it.id)}
    }

    LaunchedEffect(Unit) {
        allTags = try { repository.getAllTags() } catch (e: Exception) { emptyList() }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        OsmMapView(
            locations = filteredLocations,
            onMapReady = { mapView = it },
            onMarkerClick = { selectedLocation = it; sheetMode = SheetMode.DETAIL }
        )

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SimpleSearchBar(
                textFieldState = usernameState,
                onSearch = {},
                searchResults = searchResults,
                expanded = expanded,
                onExpandedChange = { expanded = it },
                onResultClick = { name ->
                    locations.find { it.name == name }?.let { loc ->
                        selectedLocation = loc
                        sheetMode = SheetMode.DETAIL
                        if (loc.latitude != null && loc.longitude != null) {
                            mapView?.controller?.apply {
                                setZoom(15.0)
                                animateTo(GeoPoint(loc.latitude, loc.longitude))
                            }
                        }
                    }
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
            TagsRow(
                selected = selectedTagFilters,
                onToggle = { type ->
                    selectedTagFilters = if (type in selectedTagFilters) selectedTagFilters - type else selectedTagFilters + type
                }
            )
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

    selectedLocation?.let { loc ->
        if (sheetMode == SheetMode.DETAIL) {
            LocationDetailSheet(
                location = loc,
                tags = tags,
                onDismiss = { sheetMode = SheetMode.NONE; selectedLocation = null },
                onAddInfoClick = { sheetMode = SheetMode.ADD_INFO }
            )
        } else if (sheetMode == SheetMode.ADD_INFO) {
            ModalBottomSheet(onDismissRequest = { sheetMode = SheetMode.DETAIL }) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    AddAccessibilityInfoSheet(
                        allTags = tags,
                        currentUserEmail = currentUserEmail,
                        onToggle = { tagType, checked ->
                            if (currentUserEmail.isBlank()) {
                                scope.launch {
                                    snackbarHostState.currentSnackbarData?.dismiss()
                                    snackbarHostState.showSnackbar(
                                        message = "Please log in to add accessibility info",
                                        duration = SnackbarDuration.Short
                                    )
                                }
                            } else {
                                scope.launch {
                                    try {
                                        if (checked) repository.addTag(
                                            loc.id,
                                            tagType,
                                            currentUserEmail
                                        )
                                        else repository.removeTag(loc.id, tagType, currentUserEmail)
                                        tags = repository.getTagsForLocation(loc.id)
                                        allTags = repository.getAllTags()
                                    } catch (e: Exception) {
                                        Log.e("MapScreen", "Tag toggle failed", e)
                                        snackbarHostState.showSnackbar("Something went wrong, please try again")
                                    }
                                }
                            }
                        }
                    )

                    SnackbarHost(
                        hostState = snackbarHostState,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 16.dp)
                    )
                }
            }
        }
    }
}