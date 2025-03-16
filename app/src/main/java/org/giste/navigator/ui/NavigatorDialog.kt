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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import org.giste.navigator.ui.NavigatorDialogTags.DIALOG_ACCEPT
import org.giste.navigator.ui.NavigatorDialogTags.DIALOG_CANCEL
import org.giste.navigator.ui.NavigatorDialogTags.DIALOG_MESSAGE
import org.giste.navigator.ui.NavigatorDialogTags.DIALOG_TITLE
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
        ) { }
    }
}

@Composable
fun NavigatorDialog(
    title: String,
    onCancel: () -> Unit,
    onAccept: () -> Unit,
    width: Dp = NavigatorTheme.dimensions.dialogWidth,
    innerPadding: Dp = NavigatorTheme.dimensions.marginPadding,
    iconButtonSize: Dp = NavigatorTheme.dimensions.dialogButtonIconSize,
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
    innerPadding: Dp = NavigatorTheme.dimensions.marginPadding,
) {
    Text(
        text = title,
        modifier = modifier
            .testTag(DIALOG_TITLE)
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary)
            .padding(innerPadding),
        color = MaterialTheme.colorScheme.onPrimary,
        style = MaterialTheme.typography.headlineLarge,
    )
}

@Composable
fun DialogMessage(
    message: String,
) {
    Text(
        text = message,
        modifier = Modifier
            .testTag(DIALOG_MESSAGE)
            .fillMaxWidth()
            .padding(
                top = NavigatorTheme.dimensions.marginPadding * 10,
                bottom = NavigatorTheme.dimensions.marginPadding * 10,
                start = NavigatorTheme.dimensions.marginPadding * 4,
                end = NavigatorTheme.dimensions.marginPadding * 4,
            ),
        overflow = TextOverflow.Clip,
        softWrap = true,
        style = NavigatorTheme.typography.headlineMedium,
    )
}

@Composable
fun DialogButtons(
    onAccept: () -> Unit,
    onCancel: () -> Unit,
    innerPadding: Dp = NavigatorTheme.dimensions.marginPadding,
    iconButtonSize: Dp = NavigatorTheme.dimensions.dialogButtonIconSize,
    modifier: Modifier = Modifier,
) {
    HorizontalDivider()
    Row(
        modifier = modifier
            .height(IntrinsicSize.Min)
    ) {
        Column(
            modifier = Modifier
                .testTag(DIALOG_CANCEL)
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
                .testTag(DIALOG_ACCEPT)
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
    const val DIALOG_TITLE = "DIALOG_TITLE"
    const val DIALOG_ACCEPT = "DIALOG_ACCEPT"
    const val DIALOG_CANCEL = "DIALOG_CANCEL"
    const val DIALOG_MESSAGE = "DIALOG_MESSAGE"
}