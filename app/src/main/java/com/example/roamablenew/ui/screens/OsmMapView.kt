package com.example.roamablenew.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView

@Composable
fun OsmMapView(
    modifier: Modifier = Modifier,
    latitude: Double = 51.5074,  // Default: London
    longitude: Double = -0.1278,
    zoomLevel: Double = 13.0
) {
    AndroidView(
        modifier = modifier.fillMaxSize(),
        factory = { context ->
            // Create and configure the native osmdroid MapView
            MapView(context).apply {
                setTileSource(TileSourceFactory.MAPNIK) // Standard OpenStreetMap tile style
                setMultiTouchControls(true)             // Enable pinch-to-zoom & panning

                controller.setZoom(zoomLevel)
                controller.setCenter(GeoPoint(latitude, longitude))
            }
        },
        update = { mapView ->
            // Triggered on recomposition if coordinates change dynamically
            mapView.controller.setCenter(GeoPoint(latitude, longitude))
        }
    )
}