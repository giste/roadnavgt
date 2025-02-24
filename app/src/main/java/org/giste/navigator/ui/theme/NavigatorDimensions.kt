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

package org.giste.navigator.ui.theme

import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass.Companion.Compact
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass.Companion.Expanded
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass.Companion.Medium
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class NavigatorDimensions(
    val marginPadding: Dp,
    val dialogButtonIconSize: Dp,
    val dialogKeyIconSize: Dp,
)

@Composable
fun navigatorDimensions(windowSizeClass: WindowSizeClass) = when(windowSizeClass.widthSizeClass) {
    Expanded -> navigatorExpandedDimensions
    Medium -> navigatorMediumDimensions
    Compact -> navigatorCompactDimensions
    else -> defaultNavigatorDimensions()
}

fun defaultNavigatorDimensions() = navigatorExpandedDimensions

private val navigatorExpandedDimensions = NavigatorDimensions(
    marginPadding = 4.dp,
    dialogButtonIconSize = 64.dp,
    dialogKeyIconSize = 40.dp,
)

private val navigatorMediumDimensions = NavigatorDimensions(
    marginPadding = 3.dp,
    dialogButtonIconSize = 48.dp,
    dialogKeyIconSize = 32.dp,
)

private val navigatorCompactDimensions = NavigatorDimensions(
    marginPadding = 2.dp,
    dialogButtonIconSize = 32.dp,
    dialogKeyIconSize = 24.dp,
)
