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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import org.giste.navigator.features.location.domain.Location
import org.giste.navigator.features.map.domain.MapSource
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
fun VtmMapView(
    mapSources: List<MapSource>,
    location: Location?,
    zoomLevel: Int,
    modifier: Modifier = Modifier
) {
    val mapView = rememberMapViewWithLifecycle()

    AndroidView(
        factory = {
            with(mapView.map()) {
                // Scale bar
                val mapScaleBar: MapScaleBar = DefaultMapScaleBar(this)
                val mapScaleBarLayer = MapScaleBarLayer(this, mapScaleBar)
                mapScaleBarLayer.renderer.setPosition(GLViewport.Position.BOTTOM_LEFT)
                mapScaleBarLayer.renderer.setOffset(5 * CanvasAdapter.getScale(), 0f)
                layers().add(mapScaleBarLayer)

                // Tile source from maps
                val tileSource = MultiMapFileTileSource()
                mapSources.forEach {
                    val map = MapFileTileSource()
                    map.setMapFile(it.path)
                    val result = tileSource.add(map)

                    Log.d("VtmMapView", "Added map: $it with result: $result")
                }

                // Vector layer
                val tileLayer: VectorTileLayer = setBaseMap(tileSource)

                // Building layer
                layers().add(BuildingLayer(this, tileLayer))

                // Label layer
                layers().add(LabelLayer(this, tileLayer))

                // Render theme
                setTheme(VtmThemes.DEFAULT)

                // Set centar at the bottom
                viewport().mapViewCenterY = 0.6f

                // Initial position, scale and tilt
                val initialPosition = mapPosition
                initialPosition
                    .setPosition(40.60092, -3.70806)
                .setScale((1 shl zoomLevel).toDouble())
                .setTilt(60.0f)
                mapPosition = initialPosition
                Log.d("VtmMapView", "Initial map position: $initialPosition")
            }

            mapView
        },
        modifier = modifier.fillMaxSize().focusProperties { canFocus = false },
    ) { view ->
        with(view.map()) {
            location?.let { location ->
                Log.d("VtmMapView", "Moving to $location")
                val newPosition = mapPosition
                newPosition.setPosition(location.latitude, location.longitude)
                location.bearing?.let { newPosition.setBearing(360f - location.bearing) }
                this.animator().animateTo(newPosition)
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
