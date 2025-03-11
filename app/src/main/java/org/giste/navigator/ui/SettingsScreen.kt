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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import org.giste.navigator.R
import org.giste.navigator.features.settings.domain.Settings
import org.giste.navigator.features.settings.ui.LocationMinDistanceSetting
import org.giste.navigator.features.settings.ui.LocationMinTimeSetting
import org.giste.navigator.features.settings.ui.MapZoomLevelSetting
import org.giste.navigator.features.settings.ui.PixelsToMoveRoadbookSetting
import org.giste.navigator.features.settings.ui.SettingGroup
import org.giste.navigator.ui.SettingsViewModel.UiAction.OnLocationMinDistanceChange
import org.giste.navigator.ui.SettingsViewModel.UiAction.OnLocationMinTimeChange
import org.giste.navigator.ui.SettingsViewModel.UiAction.OnMapZoomLevelChange
import org.giste.navigator.ui.SettingsViewModel.UiAction.OnPixelsToMoveRoadbookChange
import org.giste.navigator.ui.theme.NavigatorTheme

@Preview(
    name = "Tab Active 3 landscape",
    showBackground = true,
    device = "spec:width=1920px,height=1200px,dpi=360, isRound=false, orientation=landscape"
)
@Preview(
    name = "Tab Active 3 portrait",
    showBackground = true,
    device = "spec:width=1920px,height=1200px,dpi=360, isRound=false, orientation=portrait"
)
@Composable
fun SettingsScreenPreview() {
    NavigatorTheme {
        SettingsContent(
            settings = Settings(),
            uiAction = {},
            navigateBack = {}
        )
    }
}

@Composable
fun SettingsScreen(
    settingsViewModel: SettingsViewModel = viewModel(),
    navigateBack: () -> Unit,
) {
    SettingsContent(
        settings = settingsViewModel.settingsState.collectAsStateWithLifecycle().value,
        uiAction = settingsViewModel::onAction,
        navigateBack = navigateBack,
    )
}

@Composable
fun SettingsContent(
    settings: Settings,
    uiAction: (SettingsViewModel.UiAction) -> Unit,
    navigateBack: () -> Unit,
) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            val scrollState = rememberScrollState()

            Column(
                modifier = Modifier
                    .weight(1f)
                    .background(color = MaterialTheme.colorScheme.surface)
                    .verticalColumnScrollbar(scrollState)
                    .verticalScroll(scrollState)
                    .padding(NavigatorTheme.dimensions.marginPadding)
            ) {
                SettingGroup(stringResource(R.string.settings_basic_group_label))
                MapZoomLevelSetting(
                    zoomLevel = settings.mapZoomLevel,
                    onValueChange = { uiAction(OnMapZoomLevelChange(it)) },
                )
                PixelsToMoveRoadbookSetting(
                    pixelsToMove = settings.pixelsToMoveRoadbook,
                    onValueChange = { uiAction(OnPixelsToMoveRoadbookChange(it)) },
                )
                SettingGroup(stringResource(R.string.settings_advanced_group_label))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(NavigatorTheme.dimensions.marginPadding)
                ) {
                    Text(
                        text = stringResource(R.string.settings_advanced_warning),
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(color = MaterialTheme.colorScheme.errorContainer)
                            .padding(NavigatorTheme.dimensions.marginPadding),
                        color = MaterialTheme.colorScheme.onErrorContainer,
                    )
                }
                LocationMinTimeSetting(
                    minTime = settings.millisecondsBetweenLocations,
                    onValueChange = { uiAction(OnLocationMinTimeChange(it)) },
                )
                LocationMinDistanceSetting(
                    minDistance = settings.metersBetweenLocations,
                    onValueChange = { uiAction(OnLocationMinDistanceChange(it)) },
                )
            }
            ScreenBottomBar(
                title = stringResource(R.string.settings_screen_title),
                onBackClick = { navigateBack() }
            )
        }
    }
}
