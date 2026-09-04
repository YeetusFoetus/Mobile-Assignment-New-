package com.example.roamablenew.ui.screens

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
import androidx.lifecycle.compose.LifecycleEventEffect
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

data class MapMarker(
    val title: String,
    val latitude: Double,
    val longitude: Double
)

@Composable
fun OsmMapView(
    modifier: Modifier = Modifier,
    latitude: Double = 4.2105,
    longitude: Double = 101.9758,
    zoomLevel: Double = 6.0,
    markers: List<MapMarker> = emptyList(),
    onMapReady: (MapView) -> Unit = {}
) {
    val context = LocalContext.current
    val mapView = remember { MapView(context) }
    var hasCentered by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        onDispose { mapView.onDetach() }
    }

    LaunchedEffect(markers) {
        if (!hasCentered && markers.isNotEmpty()) {
            mapView.post {
                if (mapView.width > 0 && mapView.height > 0) {
                    val bounds = org.osmdroid.util.BoundingBox.fromGeoPoints(
                        markers.map { GeoPoint(it.latitude, it.longitude) }
                    )
                    mapView.zoomToBoundingBox(bounds, false, 100)
                    hasCentered = true
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
                controller.setCenter(GeoPoint(latitude, longitude)) // shown briefly while loading
            }.also { onMapReady(it) }
        },
        update = { view ->
            view.overlays.clear()
            markers.forEach { marker ->
                val osmMarker = Marker(view).apply {
                    position = GeoPoint(marker.latitude, marker.longitude)
                    title = marker.title
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                }
                view.overlays.add(osmMarker)
            }
            view.invalidate()
        }
    )
}