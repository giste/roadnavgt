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

package org.giste.navigator.features.trip.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign

const val TRIP_TOTAL = "TRIP_TOTAL"
const val TRIP_PARTIAL = "TRIP_PARTIAL"

@Composable
fun TripTotal(
    distance: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Text(
        text = distance,
        style = MaterialTheme.typography.displayMedium,
        textAlign = TextAlign.End,
        modifier = modifier
            .testTag(TRIP_TOTAL)
            .fillMaxSize()
            .wrapContentHeight()
            .clickable { onClick() }
    )
}

@Composable
fun TripPartial(
    distance: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Text(
        text = distance,
        style = MaterialTheme.typography.displayLarge,
        textAlign = TextAlign.End,
        modifier = modifier
            .testTag(TRIP_PARTIAL)
            .fillMaxSize()
            .wrapContentHeight()
            .clickable { onClick() }
    )
}
