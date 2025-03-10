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

package org.giste.navigator.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import org.giste.navigator.R
import org.giste.navigator.features.location.domain.Location
import org.giste.navigator.features.map.domain.MapSource
import org.giste.navigator.features.map.ui.MapViewer
import org.giste.navigator.features.roadbook.domain.Roadbook
import org.giste.navigator.features.roadbook.ui.Roadbook
import org.giste.navigator.features.settings.domain.Settings
import org.giste.navigator.features.trip.domain.Trip
import org.giste.navigator.features.trip.ui.TripPartial
import org.giste.navigator.features.trip.ui.TripTotal
import org.giste.navigator.ui.theme.NavigatorTheme

@Preview(
    name = "Tab Active 3",
    showBackground = true,
    device = "spec:width=1920px,height=1200px,dpi=360, isRound=false, orientation=landscape"
)
@Composable
fun NavigatorLandscapePreview() {
    NavigatorTheme(darkTheme = true) {
        NavigatorLandscapeScreen(
            locationState = null,
            mapSourceState = listOf(),
            roadbookState = Roadbook.NotLoaded,
            settings = Settings(),
            trip = Trip(),
            roadbookScrollState = LazyListState(),
            onEvent = {},
            navigateToSettings = {},
        )
    }
}

@Composable
fun NavigatorLandscapeScreen(
    locationState: Location?,
    mapSourceState: List<MapSource>,
    roadbookState: Roadbook,
    settings: Settings,
    trip: Trip,
    roadbookScrollState: LazyListState,
    onEvent: (NavigatorViewModel.UiAction) -> Unit,
    navigateToSettings: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val padding = NavigatorTheme.dimensions.marginPadding
    var showPartialSettingDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .weight(9f)
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .weight(2f)
                    .fillMaxHeight()
            ) {
                TripTotal(
                    distance = "%,.2f".format(trip.total.div(1000f)),
                    onClick = {},
                    modifier = Modifier
                        .height(IntrinsicSize.Min)
                        .padding(horizontal = padding),
                )
                HorizontalDivider()
                TripPartial(
                    distance = "%,.2f".format(trip.partial.div(1000f)),
                    onClick = { showPartialSettingDialog = true },
                    modifier = Modifier
                        .height(IntrinsicSize.Min)
                        .padding(horizontal = padding),
                )
                HorizontalDivider()
                MapViewer(
                    location = locationState,
                    mapSource = mapSourceState,
                    zoomLevel = settings.mapZoomLevel,
                )
            }
            VerticalDivider()
            Roadbook(
                roadbook = roadbookState,
                onScroll = { page, offset ->
                    onEvent(NavigatorViewModel.UiAction.SaveScroll(page, offset))
                },
                roadbookScrollState = roadbookScrollState,
                modifier = Modifier
                    .weight(5f),
            )
        }
        CommandBar(
            onEvent = onEvent,
            navigateToSettings = navigateToSettings,
        )
    }

    if (showPartialSettingDialog) {
        SetNumberDialog(
            title = stringResource(R.string.set_partial_title),
            description = stringResource(R.string.set_partial_description),
            number = trip.partial.div(10),
            numberOfIntegerDigits = 3,
            numberOfDecimalDigits = 2,
            onAccept = {
                onEvent(NavigatorViewModel.UiAction.SetPartial(it))
                showPartialSettingDialog = false
            },
            onCancel = { showPartialSettingDialog = false },
        )
    }
}
