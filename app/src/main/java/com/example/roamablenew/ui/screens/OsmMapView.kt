package com.example.roamablenew.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
    latitude: Double = 51.5074,
    longitude: Double = -0.1278,
    zoomLevel: Double = 13.0,
    markers: List<MapMarker> = emptyList(),
    onMapReady: (MapView) -> Unit = {}
) {
    val context = LocalContext.current
    val mapView = remember { MapView(context) }
    var hasCentered by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        onDispose { mapView.onDetach() }
    }

    AndroidView(
        modifier = modifier.fillMaxSize(),
        factory = { ctx ->
            org.osmdroid.config.Configuration.getInstance().userAgentValue = ctx.packageName
            org.osmdroid.config.Configuration.getInstance().osmdroidTileCache = ctx.cacheDir

            mapView.apply {
                setTileSource(StadiaTileSource(apiKey = "9f77721b-c275-46a1-9c33-d8ba737c9e9f"))
                setMultiTouchControls(true)
                controller.setZoom(zoomLevel)
                controller.setCenter(GeoPoint(latitude, longitude))
                setHorizontalMapRepetitionEnabled(false)
                setVerticalMapRepetitionEnabled(false)
                minZoomLevel = 5.0
            }.also { onMapReady(it) }
        },
        update = { view ->
            if (!hasCentered) {
                if (markers.isNotEmpty()) {
                    view.post {
                        if (view.width > 0 && view.height > 0) {
                            val bounds = org.osmdroid.util.BoundingBox.fromGeoPoints(
                                markers.map { GeoPoint(it.latitude, it.longitude) }
                            )
                            view.zoomToBoundingBox(bounds, false, 100)
                        }
                    }
                } else {
                    view.controller.setCenter(GeoPoint(latitude, longitude))
                }
                hasCentered = true
            }

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