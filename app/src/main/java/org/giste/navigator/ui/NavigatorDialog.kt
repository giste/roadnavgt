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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import org.giste.navigator.ui.NavigatorDialogTags.ACCEPT_BUTTON
import org.giste.navigator.ui.NavigatorDialogTags.CANCEL_BUTTON
import org.giste.navigator.ui.NavigatorDialogTags.TITLE
import org.giste.navigator.ui.theme.NavigatorTheme

@Preview(
    name = "Tab Active 3",
    showBackground = true,
    device = "spec:width=1920px,height=1200px,dpi=360, isRound=false, orientation=landscape"
)
@Composable
fun NavigatorDialogPreview() {
    NavigatorTheme {
        NavigatorDialog(
            title = "Title",
            onCancel = { },
            onAccept = { },
            width = 600.dp,
            height = 400.dp,
        ) { }
    }
}

@Composable
fun NavigatorDialog(
    title: String,
    onCancel: () -> Unit,
    onAccept: () -> Unit,
    width: Dp,
    height: Dp,
    innerPadding: Dp = 4.dp,
    iconButtonSize: Dp = 64.dp,
    dismissOnBackPress: Boolean = false,
    dismissOnClickOutside: Boolean = false,
    content: @Composable () -> Unit,
) {
    Dialog(
        onDismissRequest = { onCancel() },
        properties = DialogProperties(
            dismissOnBackPress = dismissOnBackPress,
            dismissOnClickOutside = dismissOnClickOutside,
            usePlatformDefaultWidth = false,
        )
    ) {
        Surface(
            modifier = Modifier
                .width(width)
                .height(IntrinsicSize.Min)
        ) {
            Column {
                DialogTitle(
                    title = title,
                    innerPadding = innerPadding,
                )

                Box(
                    modifier = Modifier
                    .weight(1f)
                ) {
                    content()
                }


                DialogButtons(
                    onAccept = onAccept,
                    onCancel = onCancel,
                    innerPadding = innerPadding,
                    iconButtonSize = iconButtonSize,
                )
            }
        }
    }
}

@Composable
fun DialogTitle(
    title: String,
    modifier: Modifier = Modifier,
    innerPadding: Dp = 4.dp,
) {
    Text(
        text = title,
        modifier = modifier
            .testTag(TITLE)
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary)
            .padding(innerPadding),
        color = MaterialTheme.colorScheme.onPrimary,
        style = MaterialTheme.typography.titleLarge,
    )
}

@Composable
fun DialogButtons(
    onAccept: () -> Unit,
    onCancel: () -> Unit,
    innerPadding: Dp = 4.dp,
    iconButtonSize: Dp = 64.dp,
    modifier: Modifier = Modifier,
) {
    HorizontalDivider()
    Row(
        modifier = modifier
            .height(IntrinsicSize.Min)
    ) {
        Column(
            modifier = Modifier
                .testTag(CANCEL_BUTTON)
                .clickable { onCancel() }
                .weight(1f)
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = "Cancel",
                modifier = Modifier.size(iconButtonSize),
            )
        }
        Column(
            modifier = Modifier
                .testTag(ACCEPT_BUTTON)
                .background(MaterialTheme.colorScheme.primary)
                .clickable { onAccept() }
                .weight(1f)
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = "Accept",
                modifier = Modifier.size(iconButtonSize),
                tint = MaterialTheme.colorScheme.onPrimary,
            )
        }
    }
}

object NavigatorDialogTags {
    const val TITLE = "TITLE"
    const val ACCEPT_BUTTON = "ACCEPT_BUTTON"
    const val CANCEL_BUTTON = "CANCEL_BUTTON"
}