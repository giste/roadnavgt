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

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import org.giste.navigator.ui.theme.NavigatorTheme

const val TRIP_TOTAL = "TRIP_TOTAL"
const val TRIP_PARTIAL = "TRIP_PARTIAL"

@Composable
fun TripTotal(
    distance: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = distance,
        style = MaterialTheme.typography.displayMedium,
        textAlign = TextAlign.End,
        modifier = modifier
            .testTag(TRIP_TOTAL)
            .height(IntrinsicSize.Min)
            .fillMaxWidth()
            .padding(NavigatorTheme.dimensions.marginPadding),
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
        modifier = modifier
            .testTag(TRIP_PARTIAL)
            .height(IntrinsicSize.Min)
            .fillMaxWidth()
            .clickable { onClick() }
            .background(color = MaterialTheme.colorScheme.primaryContainer)
            .padding(NavigatorTheme.dimensions.marginPadding),
        color = MaterialTheme.colorScheme.onPrimaryContainer,
        textAlign = TextAlign.End,
        style = MaterialTheme.typography.displayLarge.copy(fontWeight = FontWeight.Bold),
    )
}
