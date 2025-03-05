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

import android.util.Log
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.window.core.layout.WindowSizeClass

object NavigatorTheme {
    val dimensions: NavigatorDimensions
        @Composable
        @ReadOnlyComposable
        get() = localNavigatorDimensions.current

    val typography: Typography
        @Composable
        @ReadOnlyComposable
        get() = localNavigatorTypography.current

    val colors: ColorScheme
        @Composable
        @ReadOnlyComposable
        get() = localNavigatorColor.current
}

private val localNavigatorDimensions = staticCompositionLocalOf {
    defaultNavigatorDimensions()
}

private val localNavigatorTypography = staticCompositionLocalOf {
    defaultNavigatorTypography()
}

private val localNavigatorColor = staticCompositionLocalOf {
    defaultNavigatorColor()
}

@Composable
fun NavigatorTheme(
    windowSizeClass: WindowSizeClass = currentWindowAdaptiveInfo().windowSizeClass, // calculateFromSize(DpSize(853.dp, 485.dp)),
    dynamicColor: Boolean = true,
    darkTheme: Boolean = isSystemInDarkTheme(),
    dimensions: NavigatorDimensions = navigatorDimensions(windowSizeClass),
    typography: Typography = navigatorTypography(windowSizeClass),
    colorScheme: ColorScheme = navigatorColorScheme(dynamicColor, darkTheme),
    content: @Composable () -> Unit,
) {
    Log.d("NavigatorTheme", "Window size: $windowSizeClass")

    CompositionLocalProvider(
        localNavigatorDimensions provides dimensions,
        localNavigatorTypography provides typography,
        localNavigatorColor provides colorScheme,
    ) {
        MaterialTheme(
            typography = typography,
            colorScheme = colorScheme,
            content = content,
        )
    }
}
