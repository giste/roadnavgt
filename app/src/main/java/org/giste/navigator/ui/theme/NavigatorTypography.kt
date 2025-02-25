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

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import androidx.window.core.layout.WindowSizeClass
import androidx.window.core.layout.WindowWidthSizeClass.Companion.COMPACT
import androidx.window.core.layout.WindowWidthSizeClass.Companion.EXPANDED
import androidx.window.core.layout.WindowWidthSizeClass.Companion.MEDIUM

@Composable
fun navigatorTypography(windowSizeClass: WindowSizeClass) =
    when (windowSizeClass.windowWidthSizeClass) {
        EXPANDED -> navigatorTypographyExpanded
        MEDIUM -> navigatorTypographyMedium
        COMPACT -> navigatorTypographyCompact
        else -> defaultNavigatorTypography()
    }

fun defaultNavigatorTypography() = navigatorTypographyExpanded

private val navigatorTypographyExpanded = Typography(
    displayLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontSize = 64.sp,
    ),
    displayMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontSize = 51.sp,
    ),
    displaySmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontSize = 40.sp,
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontSize = 45.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontSize = 25.sp
    ),
)

private val navigatorTypographyMedium = Typography(
    displayLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontSize = 51.sp,
    ),
    displayMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontSize = 40.sp,
    ),
    displaySmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontSize = 32.sp,
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontSize = 40.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontSize = 20.sp
    ),
)

private val navigatorTypographyCompact = Typography(
    displayLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontSize = 40.sp,
    ),
    displayMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontSize = 32.sp,
    ),
    displaySmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontSize = 25.sp,
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontSize = 36.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontSize = 16.sp
    ),
)
