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

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import org.giste.navigator.R
import org.giste.navigator.ui.theme.NavigatorTheme

const val INCREASE_PARTIAL = "INCREASE_PARTIAL"
const val DECREASE_PARTIAL = "DECREASE_PARTIAL"
const val RESET_PARTIAL = "RESET_PARTIAL"
const val RESET_TRIP = "RESET_TRIP"

@Composable
fun CommandBar(
    onEvent: (NavigatorViewModel.UiAction) -> Unit,
    navigateToSettings: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val selectRoadbookLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { onEvent(NavigatorViewModel.UiAction.SetUri(uri.toString())) }
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(NavigatorTheme.dimensions.marginPadding)
    ) {
        CommandBarButton(
            onClick = { onEvent(NavigatorViewModel.UiAction.DecrementPartial) },
            icon = Icons.Default.KeyboardArrowDown,
            contentDescription = stringResource(R.string.partial_decrement_description),
            modifier = Modifier
                .weight(1f)
                .testTag(DECREASE_PARTIAL)
        )
        CommandBarButton(
            onClick = { onEvent(NavigatorViewModel.UiAction.ResetPartial) },
            icon = Icons.Default.Refresh,
            contentDescription = stringResource(R.string.partial_reset_description),
            modifier = Modifier
                .weight(1f)
                .testTag(RESET_PARTIAL)
        )
        CommandBarButton(
            onClick = { onEvent(NavigatorViewModel.UiAction.IncrementPartial) },
            icon = Icons.Default.KeyboardArrowUp,
            contentDescription = stringResource(R.string.partial_increment_description),
            modifier = Modifier
                .weight(1f)
                .testTag(INCREASE_PARTIAL)
        )
        CommandBarButton(
            onClick = { onEvent(NavigatorViewModel.UiAction.ResetTrip) },
            icon = Icons.Default.Clear,
            contentDescription = stringResource(R.string.trip_reset_description),
            modifier = Modifier
                .weight(1f)
                .testTag(RESET_TRIP)
        )
        CommandBarButton(
            onClick = { selectRoadbookLauncher.launch("application/pdf") },
            icon = Icons.Default.Search,
            contentDescription = stringResource(R.string.load_roadbook_description),
            modifier = Modifier.weight(1f)
        )
        CommandBarButton(
            onClick = { navigateToSettings() },
            icon = Icons.Default.Settings,
            contentDescription = stringResource(R.string.settings_description),
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun CommandBarButton(
    onClick: () -> Unit,
    icon: ImageVector,
    contentDescription: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            modifier = Modifier
                .size(NavigatorTheme.dimensions.commandBarIconSize),
            tint = MaterialTheme.colorScheme.onSurface
        )
    }
}
