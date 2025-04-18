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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import org.giste.navigator.R
import org.giste.navigator.features.map.domain.MapSource
import org.giste.navigator.features.map.domain.Region
import org.giste.navigator.features.settings.ui.SettingGroup
import org.giste.navigator.ui.MapManagerViewModel.UiAction.OnDelete
import org.giste.navigator.ui.MapManagerViewModel.UiAction.OnDownload
import org.giste.navigator.ui.theme.NavigatorTheme
import org.giste.navigator.util.DownloadState
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

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
fun MapManagerPreview() {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
    val lastModified = LocalDateTime.parse("2025-03-01 00:00", formatter)
        .toInstant(ZoneOffset.ofHours(0))

    NavigatorTheme {
        MapManagerContent(
            uiState = MapManagerViewModel.UiState(
                maps = listOf(
                    MapSource(Region.EUROPE, "spain.map", 0, lastModified, true),
                    MapSource(Region.EUROPE, "portugal.map", 0, lastModified, true, true),
                    MapSource(Region.EUROPE, "france.map", 0, lastModified, true, false, true),
                    MapSource(Region.EUROPE, "italy.map", 0, lastModified),
                    MapSource(Region.EUROPE, "switzerland.map", 0, lastModified),
                ),
                downloads = mapOf("europe/switzerland.map" to DownloadState.Downloading(26))
            ),
            uiAction = {},
            navigateBack = {},
        )
    }
}

@Composable
fun MapManagerScreen(
    mapManagerViewModel: MapManagerViewModel = viewModel(),
    navigateBack: () -> Unit,
) {
    MapManagerContent(
        uiState = mapManagerViewModel.uiState.collectAsStateWithLifecycle().value,
        uiAction = mapManagerViewModel::onAction,
        navigateBack = navigateBack,
    )
}

@Composable
fun MapManagerContent(
    uiState: MapManagerViewModel.UiState,
    uiAction: (MapManagerViewModel.UiAction) -> Unit,
    navigateBack: () -> Unit,
) {
    val availableMaps by rememberSaveable(uiState.maps) {
        val maps = mutableMapOf<Region, List<MapSource>>()
        Region.entries.forEach { region ->
            maps.put(
                key = region,
                value = uiState.maps.filter { map -> map.region == region && !map.downloaded }
                    .sortedBy { it.fileName }
            )
        }

        mutableStateOf(maps.toMap())
    }
    val downloadedMaps by rememberSaveable(uiState.maps) {
        mutableStateOf(uiState.maps.filter { it.downloaded }.sortedBy { it.fileName })
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(NavigatorTheme.dimensions.marginPadding)
            ) {
                // Downloaded maps
                item {
                    SettingGroup(stringResource(R.string.map_manager_downloaded_label))
                }
                items(
                    items = downloadedMaps,
                    key = { it.id }
                ) {
                    MapRow(
                        name = it.fileName.removeSuffix(".map"),
                        downloaded = true,
                        updatable = it.updatable,
                        obsolete = it.obsolete,
                        onDownload = { uiAction(OnDownload(it)) },
                        onDelete = { uiAction(OnDelete(it)) },
                        downloadState = null,
                    )
                }
                // Available maps
                availableMaps.forEach {
                    item {
                        RegionGroup(
                            name = getRegionName(it.key),
                            mapSources = it.value,
                            uiAction = uiAction,
                            downloads = uiState.downloads,
                        )
                    }
                }
            }
            ScreenBottomBar(
                title = stringResource(R.string.map_manager_screen_title),
                onBackClick = { navigateBack() }
            )
        }

    }
}

@Composable
fun RegionGroup(
    name: String,
    mapSources: List<MapSource>,
    uiAction: (MapManagerViewModel.UiAction) -> Unit,
    downloads: Map<String, DownloadState>,
    modifier: Modifier = Modifier,
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                start = NavigatorTheme.dimensions.marginPadding,
                end = NavigatorTheme.dimensions.marginPadding,
                top = NavigatorTheme.dimensions.marginPadding.times(4),
                bottom = NavigatorTheme.dimensions.marginPadding
            ),
    ) {
        Row {
            Text(
                text = name,
                modifier = Modifier
                    .padding(start = NavigatorTheme.dimensions.marginPadding)
                    .weight(1f),
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
            )
            CommandBarButton(
                onClick = { expanded = !expanded },
                icon = if (expanded)
                    Icons.Default.KeyboardArrowUp
                else
                    Icons.Default.KeyboardArrowDown
            )
        }

        HorizontalDivider()

        if (expanded) {
            mapSources.forEach {
                MapRow(
                    name = it.fileName.removeSuffix(".map"),
                    onDownload = { uiAction(OnDownload(it)) },
                    onDelete = {},
                    downloadState = downloads[it.id]
                )
            }
        }
    }

}

@Composable
fun MapRow(
    name: String,
    downloaded: Boolean = false,
    updatable: Boolean = false,
    obsolete: Boolean = false,
    onDownload: () -> Unit,
    onDelete: () -> Unit,
    downloadState: DownloadState?,
) {
    Row(
        modifier = Modifier
            .height(IntrinsicSize.Min)
            .padding(NavigatorTheme.dimensions.marginPadding),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = name,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.titleLarge,
        )
        if (downloaded) {
            if (obsolete) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.obsolete),
                    contentDescription = null,
                    modifier = Modifier
                        .size(NavigatorTheme.dimensions.commandBarIconSize),
                )
            }
            if (updatable) {
                CommandBarButton(
                    onClick = { onDownload() },
                    icon = Icons.Default.Refresh
                )
            }
            CommandBarButton(
                onClick = { onDelete() },
                icon = Icons.Default.Delete,
            )
        } else {
            if (downloadState is DownloadState.Downloading) {
                CircularProgressIndicator(
                    progress = { downloadState.percentage.div(100f) },
                )
            } else {
                CommandBarButton(
                    onClick = { onDownload() },
                    icon = ImageVector.vectorResource(R.drawable.download),
                )
            }
        }
    }
    HorizontalDivider()
}

@Composable
fun getRegionName(region: Region): String = when (region) {
    Region.AFRICA -> stringResource(R.string.map_region_africa)
    Region.ASIA -> stringResource(R.string.map_region_asia)
    Region.CHINA -> stringResource(R.string.map_region_china)
    Region.AUSTRALIA_OCEANIA -> stringResource(R.string.map_region_australia_oceania)
    Region.CENTRAL_AMERICA -> stringResource(R.string.map_region_central_america)
    Region.EUROPE -> stringResource(R.string.map_region_europe)
    Region.NORTH_AMERICA -> stringResource(R.string.map_region_north_america)
    Region.CANADA -> stringResource(R.string.map_region_canada)
    Region.RUSSIA -> stringResource(R.string.map_region_russia)
    Region.SOUTH_AMERICA -> stringResource(R.string.map_region_south_america)
}