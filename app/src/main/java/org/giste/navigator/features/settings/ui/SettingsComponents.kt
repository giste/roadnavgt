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

package org.giste.navigator.features.settings.ui

import android.icu.text.DecimalFormatSymbols
import android.icu.text.NumberFormat
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import org.giste.navigator.R
import org.giste.navigator.ui.SetNumberDialog
import org.giste.navigator.ui.theme.NavigatorTheme

@Composable
fun MapZoomLevelSetting(
    zoomLevel: Int,
    onValueChange: (Int) -> Unit,
) {
    var sliderPosition by rememberSaveable {
        mutableFloatStateOf(zoomLevel.coerceAtLeast(15).coerceAtMost(20).toFloat())
    }

    Setting(
        label = stringResource(R.string.settings_zoom_level_label),
        description = stringResource(R.string.settings_zoom_level_description)
    ) {
        Slider(
            value = sliderPosition,
            onValueChange = { sliderPosition = it },
            valueRange = 15f..20f,
            steps = 4,
            onValueChangeFinished = { onValueChange(sliderPosition.toInt()) }
        )
    }
}

@Composable
fun LocationMinDistanceSetting(
    minDistance: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    NumberSetting(
        number = minDistance,
        onValueChange = onValueChange,
        numberOfIntegerDigits = 2,
        numberOfFractionalDigits = 0,
        label = stringResource(R.string.settings_minimum_distance_label),
        description = stringResource(R.string.settings_minimum_distance_description),
        modifier = modifier,
    )
}

@Composable
fun LocationMinTimeSetting(
    minTime: Long,
    onValueChange: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    NumberSetting(
        number = minTime.toInt(),
        onValueChange = { onValueChange(it.toLong()) },
        numberOfIntegerDigits = 4,
        numberOfFractionalDigits = 0,
        label = stringResource(R.string.settings_minimum_time_label),
        description = stringResource(R.string.settings_minimum_time_description),
        modifier = modifier,
    )
}

@Composable
fun NumberSetting(
    number: Int,
    onValueChange: (Int) -> Unit,
    numberOfIntegerDigits: Int,
    numberOfFractionalDigits: Int,
    label: String,
    description: String,
    modifier: Modifier = Modifier,
    decimalFormatSymbols: DecimalFormatSymbols = DecimalFormatSymbols.getInstance(),
) {
    var showNumberDialog by rememberSaveable { mutableStateOf(false) }

    Setting(
        label = label,
        description = description,
        modifier = modifier,
    ) {
        val numberFormat by rememberSaveable {
            val nf = NumberFormat.getInstance(decimalFormatSymbols.locale)
            with(nf) {
                isGroupingUsed = true
                minimumFractionDigits = numberOfFractionalDigits
                maximumFractionDigits = numberOfFractionalDigits
                minimumIntegerDigits = 1
                maximumIntegerDigits = numberOfIntegerDigits
            }
            mutableStateOf(nf)
        }

        Box(
            modifier = modifier
                .clickable { showNumberDialog = true },
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = numberFormat.format(number),
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = TextFieldDefaults.colors().unfocusedContainerColor)
                    .padding(NavigatorTheme.dimensions.marginPadding * 4),
                textAlign = TextAlign.End,
                style = MaterialTheme.typography.titleLarge,
            )
        }
    }

    if (showNumberDialog) {
        SetNumberDialog(
            title = stringResource(R.string.settings_minimum_distance_label),
            description = stringResource(R.string.settings_minimum_distance_description),
            number = number,
            numberOfIntegerDigits = numberOfIntegerDigits,
            numberOfDecimalDigits = numberOfFractionalDigits,
            onAccept = {
                onValueChange(it)
                showNumberDialog = false
            },
            onCancel = { showNumberDialog = false },
        )
    }
}

@Composable
fun SettingGroup(
    name: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                start = NavigatorTheme.dimensions.marginPadding,
                end = NavigatorTheme.dimensions.marginPadding,
                top = NavigatorTheme.dimensions.marginPadding.times(4),
                bottom = NavigatorTheme.dimensions.marginPadding
            ),
    ) {
        Column {
            Text(
                text = name,
                modifier = Modifier.padding(start = NavigatorTheme.dimensions.marginPadding),
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
            )
            HorizontalDivider()
        }
    }
}

@Composable
fun Setting(
    label: String,
    description: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .padding(NavigatorTheme.dimensions.marginPadding),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier
                .weight(7f)
                .padding(NavigatorTheme.dimensions.marginPadding),
        ) {
            // Label
            Text(
                text = label,
                style = MaterialTheme.typography.titleLarge
            )
            // Description
            Text(
                text = description,
                style = MaterialTheme.typography.bodyLarge
            )
        }
        Column(
            modifier = Modifier
                .weight(3f)
                .padding(NavigatorTheme.dimensions.marginPadding),
            verticalArrangement = Arrangement.Center
        ) { content() }
    }
}
