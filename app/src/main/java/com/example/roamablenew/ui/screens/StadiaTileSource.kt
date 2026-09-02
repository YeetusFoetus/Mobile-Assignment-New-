package com.example.roamablenew.ui.screens

import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase
//import org.osmdroid.tileprovider.util.MapTileIndex
import org.osmdroid.util.MapTileIndex

class StadiaTileSource(private val apiKey: String) : OnlineTileSourceBase(
    "StadiaAlidadeSmooth",
    0, 19, 256, ".png",
    arrayOf("https://tiles.stadiamaps.com/tiles/alidade_smooth/")
) {
    override fun getTileURLString(pMapTileIndex: Long): String {
        val zoom = MapTileIndex.getZoom(pMapTileIndex)
        val x = MapTileIndex.getX(pMapTileIndex)
        val y = MapTileIndex.getY(pMapTileIndex)
        return "${baseUrl}$zoom/$x/$y.png?api_key=$apiKey"
    }
}