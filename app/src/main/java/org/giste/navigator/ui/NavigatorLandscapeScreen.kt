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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import org.giste.navigator.R
import org.giste.navigator.features.location.domain.Location
import org.giste.navigator.features.map.ui.MapViewer
import org.giste.navigator.features.roadbook.domain.Roadbook
import org.giste.navigator.features.roadbook.ui.Roadbook
import org.giste.navigator.features.settings.domain.Settings
import org.giste.navigator.features.trip.TripPartial
import org.giste.navigator.features.trip.TripTotal
import org.giste.navigator.features.trip.domain.Trip
import org.giste.navigator.ui.theme.NavigatorTheme

@Preview(
    name = "Tab Active 3",
    showBackground = true,
    device = "spec:width=1920px,height=1200px,dpi=360, isRound=false, orientation=landscape"
)
@Composable
fun NavigatorLandscapePreview() {
    NavigatorTheme {
        NavigatorLandscapeScreen(
            locationState = null,
            mapState = listOf(),
            roadbookState = Roadbook.NotLoaded,
            settings = Settings(),
            trip = Trip(),
            onEvent = {},
        )
    }
}

@Composable
fun NavigatorLandscapeScreen(
    locationState: Location?,
    mapState: List<String>,
    roadbookState: Roadbook,
    settings: Settings,
    trip: Trip,
    onEvent: (NavigatorViewModel.UiAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val padding = NavigatorTheme.dimensions.marginPadding
    val showPartialSettingDialog = remember { mutableStateOf(false) }

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
                    .focusProperties { canFocus = false }
                    .fillMaxHeight()
            ) {
                TripTotal(
                    distance = "%,.2f".format(trip.total.div(1000f)),
                    onClick = {},
                    modifier = Modifier
                        .weight(.9f)
                        .padding(horizontal = padding),
                )
                HorizontalDivider()
                TripPartial(
                    distance = "%,.2f".format(trip.partial.div(1000f)),
                    onClick = { showPartialSettingDialog.value = true },
                    modifier = Modifier
                        .weight(1.2f)
                        .padding(horizontal = padding),
                )
                HorizontalDivider()
                MapViewer(
                    location = locationState,
                    map = mapState,
                    modifier = Modifier
                        .weight(5f)
                        .focusProperties { canFocus = false },
                )
            }
            VerticalDivider()
            Roadbook(
                roadbookState = roadbookState,
                onScroll = { page, offset ->
                    onEvent(NavigatorViewModel.UiAction.SaveScroll(page, offset))
                },
                modifier = Modifier
                    .weight(5f),
            )
        }
        CommandBar(
            settings = settings,
            onEvent = onEvent,
            modifier = Modifier.weight(1f).focusProperties { canFocus = false }
        )
    }

    if (showPartialSettingDialog.value) {
        SetNumberDialog(
            showDialog = showPartialSettingDialog,
            title = stringResource(R.string.set_partial_title),
            description = stringResource(R.string.set_partial_description),
            number = trip.partial.div(10),
            numberOfIntegerDigits = 3,
            numberOfDecimalDigits = 2,
            onAccept = { onEvent(NavigatorViewModel.UiAction.SetPartial(it)) },
        )
    }

}
