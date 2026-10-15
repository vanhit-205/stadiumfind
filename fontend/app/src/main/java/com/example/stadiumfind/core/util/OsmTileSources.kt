package com.example.stadiumfind.core.util

import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase
import org.osmdroid.tileprovider.tilesource.XYTileSource

/**
 * Nguồn OpenStreetMap miễn phí 100%, không cần API Key, không bị chặn IP 403:
 * - OSM_PUBLIC: Sử dụng server mirror của cộng đồng FOSSGIS OpenStreetMap Đức.
 *   Dữ liệu bản đồ giống hệt 100% với OpenStreetMap quốc tế nhưng có hạ tầng máy chủ riêng,
 *   không bị ảnh hưởng bởi danh sách cấm IP (403 Access Blocked) của OSM Foundation.
 */
object OsmTileSources {

    val OSM_PUBLIC: OnlineTileSourceBase = XYTileSource(
        "OSMPublicTile",
        0,
        19,
        256,
        ".png",
        arrayOf(
            "https://tile.openstreetmap.de/"
        ),
        "© OpenStreetMap contributors"
    )
}
