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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import org.giste.navigator.features.location.domain.Location
import org.giste.navigator.features.map.domain.MapSource

@Composable
fun MapViewer(
    mapSource: List<MapSource>,
    location: Location?,
    zoomLevel: Int,
    modifier: Modifier = Modifier,
) {
    Log.d("Map", "Location: $location")

    if (mapSource.isEmpty()) {
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
            modifier = modifier.fillMaxSize(),
        ) {
            VtmMapView(
                mapSources = mapSource,
                location = location,
                zoomLevel = zoomLevel,
                modifier = modifier,
            )
        }
    }
}

