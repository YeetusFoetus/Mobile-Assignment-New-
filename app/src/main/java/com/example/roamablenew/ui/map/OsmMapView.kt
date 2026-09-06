package com.example.roamablenew.ui.map

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import kotlin.collections.isNotEmpty
import kotlin.collections.mapNotNull
import com.example.roamablenew.data.Location

@Composable
fun OsmMapView(
    modifier: Modifier = Modifier,
    latitude: Double = 4.2105,
    longitude: Double = 101.9758,
    zoomLevel: Double = 6.0,
    locations: List<Location> = emptyList(),
    onMapReady: (MapView) -> Unit = {},
    onMarkerClick: (Location) -> Unit = {}
) {
    val context = LocalContext.current
    val mapView = remember { MapView(context) }
    var hasCentered by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        onDispose { mapView.onDetach() }
    }

    LaunchedEffect(locations) {
        if (!hasCentered && locations.isNotEmpty()) {
            mapView.post {
                if (mapView.width > 0 && mapView.height > 0) {
                    val points = locations.mapNotNull {
                        if (it.latitude != null && it.longitude != null)
                            GeoPoint(it.latitude, it.longitude) else null
                    }
                    if (points.isNotEmpty()) {
                        mapView.zoomToBoundingBox(BoundingBox.fromGeoPoints(points), false, 100)
                        hasCentered = true
                    }
                }
            }
        }
    }

    AndroidView(
        modifier = modifier.fillMaxSize(),
        factory = {
            mapView.apply {
                setTileSource(StadiaTileSource(apiKey = "9f77721b-c275-46a1-9c33-d8ba737c9e9f"))
                setMultiTouchControls(true)
                setHorizontalMapRepetitionEnabled(false)
                setVerticalMapRepetitionEnabled(false)
                minZoomLevel = 5.0
                controller.setZoom(zoomLevel)
                controller.setCenter(GeoPoint(latitude, longitude))
            }.also { onMapReady(it) }
        },
        update = { view ->
            view.overlays.clear()
            locations.forEach { loc ->
                if (loc.latitude != null && loc.longitude != null) {
                    val marker = Marker(view).apply {
                        position = GeoPoint(loc.latitude, loc.longitude)
                        title = loc.name
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        setOnMarkerClickListener { _, _ ->
                            onMarkerClick(loc)
                            true // consume tap, skip osmdroid's default title bubble
                        }
                    }
                    view.overlays.add(marker)
                }
            }
            view.invalidate()
        }
    )
}