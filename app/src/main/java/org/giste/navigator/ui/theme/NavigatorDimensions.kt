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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass
import androidx.window.core.layout.WindowWidthSizeClass.Companion.COMPACT
import androidx.window.core.layout.WindowWidthSizeClass.Companion.EXPANDED
import androidx.window.core.layout.WindowWidthSizeClass.Companion.MEDIUM

@Immutable
data class NavigatorDimensions(
    val marginPadding: Dp,
    val dialogButtonIconSize: Dp,
    val dialogKeyIconSize: Dp,
    val dialogWidth: Dp,
    val minClickableSize: Dp,
    val commandBarIconSize: Dp,
)

@Composable
fun navigatorDimensions(windowSizeClass: WindowSizeClass) = when(windowSizeClass.windowWidthSizeClass) {
    EXPANDED -> navigatorExpandedDimensions
    MEDIUM -> navigatorMediumDimensions
    COMPACT -> navigatorCompactDimensions
    else -> defaultNavigatorDimensions()
}

fun defaultNavigatorDimensions() = navigatorExpandedDimensions

private val navigatorExpandedDimensions = NavigatorDimensions(
    marginPadding = 4.dp,
    dialogButtonIconSize = 72.dp,
    dialogKeyIconSize = 40.dp,
    dialogWidth = 750.dp,
    minClickableSize = 80.dp,
    commandBarIconSize = 40.dp,
)

private val navigatorMediumDimensions = NavigatorDimensions(
    marginPadding = 3.dp,
    dialogButtonIconSize = 64.dp,
    dialogKeyIconSize = 32.dp,
    dialogWidth = 600.dp,
    minClickableSize = 72.dp,
    commandBarIconSize = 32.dp,
)

private val navigatorCompactDimensions = NavigatorDimensions(
    marginPadding = 2.dp,
    dialogButtonIconSize = 56.dp,
    dialogKeyIconSize = 24.dp,
    dialogWidth = 450.dp,
    minClickableSize = 64.dp,
    commandBarIconSize = 24.dp,
)
