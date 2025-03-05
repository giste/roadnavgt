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

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.text.isDigitsOnly
import org.giste.navigator.R
import org.giste.navigator.features.settings.domain.Settings
import org.giste.navigator.ui.NavigatorDialog
import org.giste.navigator.ui.theme.NavigatorTheme

// TODO("Transform in screen")

@Preview(
    name = "Tab Active 3",
    showBackground = true,
    device = "spec:width=1920px,height=1200px,dpi=360, isRound=false, orientation=landscape"
)
@Composable
fun SettingsDialogPreview() {
    NavigatorTheme {
        SettingsDialog(
            title = "Settings",
            settings = Settings(),
            onAccept = { },
            onCancel = { },
        )
    }
}

@Composable
fun SettingsDialog(
    title: String,
    settings: Settings,
    onAccept: (Settings) -> Unit,
    onCancel: () -> Unit,
) {
    var minTime by rememberSaveable { mutableLongStateOf(settings.locationMinTime) }
    var minDistance by rememberSaveable { mutableIntStateOf(settings.locationMinDistance) }

    NavigatorDialog(
        title = title,
        onCancel = onCancel,
        onAccept = { onAccept(Settings(minTime, minDistance)) },
        width = NavigatorTheme.dimensions.dialogWidth,
        height = 400.dp,
        innerPadding = NavigatorTheme.dimensions.marginPadding,
        iconButtonSize = NavigatorTheme.dimensions.dialogButtonIconSize,
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(NavigatorTheme.dimensions.marginPadding),
        ) {
            MinTimeSetting(
                minTime = minTime,
                onValueChange = { minTime = it }
            )
            MinDistanceSetting(
                minDistance = minDistance,
                onValueChange = { minDistance = it }
            )
        }
    }

}

@Composable
fun MinTimeSetting(
    minTime: Long,
    onValueChange: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    SettingRow(
        label = stringResource(R.string.settings_min_time_label),
        modifier = modifier
    ) {
        TextField(
            value = minTime.toString(),
            onValueChange = { onValueChange(it.toLong()) },
            textStyle = MaterialTheme.typography.displaySmall.copy(
                textAlign = TextAlign.End,
            ),
            singleLine = true,
        )
    }
}

@Composable
fun MinDistanceSetting(
    minDistance: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    SettingRow(
        label = stringResource(R.string.settings_min_distance_label),
        modifier = modifier
    ) {
        TextField(
            value = minDistance.toString(),
            onValueChange = { onValueChange(it.toInt()) },
            textStyle = MaterialTheme.typography.displaySmall.copy(
                textAlign = TextAlign.End,
            ),
            singleLine = true,
        )
    }
}

@Composable
fun SettingRow(
    label: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.height(IntrinsicSize.Min),
    ) {
        Text(
            text = label,
            modifier = Modifier.weight(7f),
            style = MaterialTheme.typography.displaySmall,
        )
        Box(
            modifier = Modifier.weight(3f),
            contentAlignment = Alignment.CenterEnd,
        ) {
            content()
        }
    }
}
