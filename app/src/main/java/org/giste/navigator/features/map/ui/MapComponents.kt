/*
 * Copyright 2025 Giste Trappiste
 *
 * This program is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */

package org.giste.navigator.features.map.ui

import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import org.giste.navigator.features.location.domain.Location
import org.oscim.android.MapView
import org.oscim.backend.CanvasAdapter
import org.oscim.layers.tile.buildings.BuildingLayer
import org.oscim.layers.tile.vector.VectorTileLayer
import org.oscim.layers.tile.vector.labeling.LabelLayer
import org.oscim.renderer.GLViewport
import org.oscim.scalebar.DefaultMapScaleBar
import org.oscim.scalebar.MapScaleBar
import org.oscim.scalebar.MapScaleBarLayer
import org.oscim.theme.internal.VtmThemes
import org.oscim.tiling.source.mapfile.MapFileTileSource
import org.oscim.tiling.source.mapfile.MultiMapFileTileSource

@Composable
fun MapViewer(
    map: List<String>,
    location: Location?,
    modifier: Modifier = Modifier,
) {
    Log.d("Map", "Location: $location")

    if (map.isEmpty()) {
        Text(
            text = location?.toString() ?: "Map",
            modifier = modifier
                .fillMaxSize()
                .wrapContentHeight(),
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
            style = if (location == null) {
                MaterialTheme.typography.displayLarge
            } else {
                MaterialTheme.typography.labelMedium
            },
            textAlign = TextAlign.Center,
        )
    } else {
        Surface(
            modifier = modifier.fillMaxSize().focusProperties { canFocus = false },
        ) {
            VtmMapView(
                maps = map,
                location = location,
                modifier = modifier,
            )
        }
    }
}

@Composable
fun VtmMapView(
    maps: List<String>,
    location: Location?,
    modifier: Modifier = Modifier
) {
    val mapView = rememberMapViewWithLifecycle()

    AndroidView(
        factory = {
            with(mapView.map()) {
                // Scale bar
                val mapScaleBar: MapScaleBar = DefaultMapScaleBar(mapView.map())
                val mapScaleBarLayer = MapScaleBarLayer(mapView.map(), mapScaleBar)
                mapScaleBarLayer.renderer.setPosition(GLViewport.Position.BOTTOM_LEFT)
                mapScaleBarLayer.renderer.setOffset(5 * CanvasAdapter.getScale(), 0f)
                mapView.map().layers().add(mapScaleBarLayer)

                // Tile source from maps
                val tileSource = MultiMapFileTileSource()
                maps.forEach {
                    val map = MapFileTileSource()
                    map.setMapFile(it)
                    val result = tileSource.add(map)

                    Log.d("VtmMapView", "Added map: $it with result: $result")
                }

                // Vector layer
                val tileLayer: VectorTileLayer = mapView.map().setBaseMap(tileSource)

                // Building layer
                layers().add(BuildingLayer(mapView.map(), tileLayer))

                // Label layer
                layers().add(LabelLayer(mapView.map(), tileLayer))

                // Render theme
                setTheme(VtmThemes.DEFAULT)

                // Initial position, scale and tilt
                val mapPosition = this.mapPosition
                mapPosition
                    .setPosition(40.60092, -3.70806)
                    .setScale((1 shl 19).toDouble())
                    .setTilt(60.0f)
                setMapPosition(mapPosition)
                Log.d("VtmMapView", "Initial map position: ${this.mapPosition}")
            }

            mapView
        },
        modifier = modifier.fillMaxSize().focusProperties { canFocus = false },
    ) { view ->
        with(view.map()) {
            location?.let { location ->
                val mapPosition = this.mapPosition
                mapPosition.setPosition(location.latitude, location.longitude)
                location.bearing?.let { mapPosition.setBearing(location.bearing) }
                this.animator().animateTo(mapPosition)
            }
        }
    }
}

@Composable
private fun rememberMapViewWithLifecycle(): MapView {
    val context = LocalContext.current
    val mapView = remember { MapView(context) }
    val observer = remember { VtmMapViewLifecycleObserver(mapView) }
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    DisposableEffect(Unit) {
        lifecycle.addObserver(observer)
        onDispose {
            lifecycle.removeObserver(observer)
        }
    }

    return mapView
}

private class VtmMapViewLifecycleObserver(private val mapView: MapView) : DefaultLifecycleObserver {
    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        mapView.onResume()
    }

    override fun onPause(owner: LifecycleOwner) {
        super.onPause(owner)
        mapView.onPause()


    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        mapView.onDestroy()
    }
}
