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

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import org.giste.navigator.ui.theme.NavigatorTheme

@Preview(
    name = "Tab Active 3 Landscape",
    showBackground = true,
    device = "spec:width=853dp, height=485dp, isRound=false, orientation=landscape",
)
@Composable
fun NavigatorAlertPreview() {
    NavigatorTheme {
        NavigatorAlertDialog(
            title = "Title",
            message = "Alert message to display",
            onCancel = {},
            onAccept = {},
        )
    }
}

@Composable
fun NavigatorAlertDialog(
    title: String,
    message: String,
    onCancel: () -> Unit,
    onAccept: () -> Unit,
    width: Dp = NavigatorTheme.dimensions.dialogWidth,
    innerPadding: Dp = NavigatorTheme.dimensions.marginPadding,
    iconButtonSize: Dp = NavigatorTheme.dimensions.dialogButtonIconSize,
    dismissOnBackPress: Boolean = false,
    dismissOnClickOutside: Boolean = false,
) {
    NavigatorDialog(
        title = title,
        onCancel = onCancel,
        onAccept = onAccept,
        width = width,
        innerPadding = innerPadding,
        iconButtonSize = iconButtonSize,
        dismissOnBackPress = dismissOnBackPress,
        dismissOnClickOutside = dismissOnClickOutside
    ) {
        DialogMessage(message)
    }
}