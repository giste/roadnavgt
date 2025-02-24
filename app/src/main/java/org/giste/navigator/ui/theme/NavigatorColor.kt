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

import android.os.Build
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

val primaryLight = Color(0xFF835415)
val onPrimaryLight = Color(0xFFFFFFFF)
val primaryContainerLight = Color(0xFFFFDDBA)
val onPrimaryContainerLight = Color(0xFF2B1700)
val secondaryLight = Color(0xFF715A41)
val onSecondaryLight = Color(0xFFFFFFFF)
val secondaryContainerLight = Color(0xFFFDDDBD)
val onSecondaryContainerLight = Color(0xFF281805)
val tertiaryLight = Color(0xFF55633C)
val onTertiaryLight = Color(0xFFFFFFFF)
val tertiaryContainerLight = Color(0xFFD9E9B7)
val onTertiaryContainerLight = Color(0xFF141F02)
val errorLight = Color(0xFFBA1A1A)
val onErrorLight = Color(0xFFFFFFFF)
val errorContainerLight = Color(0xFFFFDAD6)
val onErrorContainerLight = Color(0xFF410002)
val backgroundLight = Color(0xFFFFF8F4)
val onBackgroundLight = Color(0xFF211A13)
val surfaceLight = Color(0xFFFFF8F4)
val onSurfaceLight = Color(0xFF211A13)
val surfaceVariantLight = Color(0xFFF1DFD0)
val onSurfaceVariantLight = Color(0xFF50453A)
val outlineLight = Color(0xFF827568)
val outlineVariantLight = Color(0xFFD4C4B5)
val scrimLight = Color(0xFF000000)
val inverseSurfaceLight = Color(0xFF372F27)
val inverseOnSurfaceLight = Color(0xFFFDEEE3)
val inversePrimaryLight = Color(0xFFF9BA72)
val surfaceDimLight = Color(0xFFE5D8CC)
val surfaceBrightLight = Color(0xFFFFF8F4)
val surfaceContainerLowestLight = Color(0xFFFFFFFF)
val surfaceContainerLowLight = Color(0xFFFFF1E6)
val surfaceContainerLight = Color(0xFFFAEBE0)
val surfaceContainerHighLight = Color(0xFFF4E6DA)
val surfaceContainerHighestLight = Color(0xFFEEE0D5)

val primaryLightMediumContrast = Color(0xFF613A00)
val onPrimaryLightMediumContrast = Color(0xFFFFFFFF)
val primaryContainerLightMediumContrast = Color(0xFF9D6A2A)
val onPrimaryContainerLightMediumContrast = Color(0xFFFFFFFF)
val secondaryLightMediumContrast = Color(0xFF543F28)
val onSecondaryLightMediumContrast = Color(0xFFFFFFFF)
val secondaryContainerLightMediumContrast = Color(0xFF897056)
val onSecondaryContainerLightMediumContrast = Color(0xFFFFFFFF)
val tertiaryLightMediumContrast = Color(0xFF3A4723)
val onTertiaryLightMediumContrast = Color(0xFFFFFFFF)
val tertiaryContainerLightMediumContrast = Color(0xFF6B7A50)
val onTertiaryContainerLightMediumContrast = Color(0xFFFFFFFF)
val errorLightMediumContrast = Color(0xFF8C0009)
val onErrorLightMediumContrast = Color(0xFFFFFFFF)
val errorContainerLightMediumContrast = Color(0xFFDA342E)
val onErrorContainerLightMediumContrast = Color(0xFFFFFFFF)
val backgroundLightMediumContrast = Color(0xFFFFF8F4)
val onBackgroundLightMediumContrast = Color(0xFF211A13)
val surfaceLightMediumContrast = Color(0xFFFFF8F4)
val onSurfaceLightMediumContrast = Color(0xFF211A13)
val surfaceVariantLightMediumContrast = Color(0xFFF1DFD0)
val onSurfaceVariantLightMediumContrast = Color(0xFF4C4136)
val outlineLightMediumContrast = Color(0xFF695D51)
val outlineVariantLightMediumContrast = Color(0xFF86786B)
val scrimLightMediumContrast = Color(0xFF000000)
val inverseSurfaceLightMediumContrast = Color(0xFF372F27)
val inverseOnSurfaceLightMediumContrast = Color(0xFFFDEEE3)
val inversePrimaryLightMediumContrast = Color(0xFFF9BA72)
val surfaceDimLightMediumContrast = Color(0xFFE5D8CC)
val surfaceBrightLightMediumContrast = Color(0xFFFFF8F4)
val surfaceContainerLowestLightMediumContrast = Color(0xFFFFFFFF)
val surfaceContainerLowLightMediumContrast = Color(0xFFFFF1E6)
val surfaceContainerLightMediumContrast = Color(0xFFFAEBE0)
val surfaceContainerHighLightMediumContrast = Color(0xFFF4E6DA)
val surfaceContainerHighestLightMediumContrast = Color(0xFFEEE0D5)

val primaryLightHighContrast = Color(0xFF341D00)
val onPrimaryLightHighContrast = Color(0xFFFFFFFF)
val primaryContainerLightHighContrast = Color(0xFF613A00)
val onPrimaryContainerLightHighContrast = Color(0xFFFFFFFF)
val secondaryLightHighContrast = Color(0xFF301F0A)
val onSecondaryLightHighContrast = Color(0xFFFFFFFF)
val secondaryContainerLightHighContrast = Color(0xFF543F28)
val onSecondaryContainerLightHighContrast = Color(0xFFFFFFFF)
val tertiaryLightHighContrast = Color(0xFF1A2605)
val onTertiaryLightHighContrast = Color(0xFFFFFFFF)
val tertiaryContainerLightHighContrast = Color(0xFF3A4723)
val onTertiaryContainerLightHighContrast = Color(0xFFFFFFFF)
val errorLightHighContrast = Color(0xFF4E0002)
val onErrorLightHighContrast = Color(0xFFFFFFFF)
val errorContainerLightHighContrast = Color(0xFF8C0009)
val onErrorContainerLightHighContrast = Color(0xFFFFFFFF)
val backgroundLightHighContrast = Color(0xFFFFF8F4)
val onBackgroundLightHighContrast = Color(0xFF211A13)
val surfaceLightHighContrast = Color(0xFFFFF8F4)
val onSurfaceLightHighContrast = Color(0xFF000000)
val surfaceVariantLightHighContrast = Color(0xFFF1DFD0)
val onSurfaceVariantLightHighContrast = Color(0xFF2B2218)
val outlineLightHighContrast = Color(0xFF4C4136)
val outlineVariantLightHighContrast = Color(0xFF4C4136)
val scrimLightHighContrast = Color(0xFF000000)
val inverseSurfaceLightHighContrast = Color(0xFF372F27)
val inverseOnSurfaceLightHighContrast = Color(0xFFFFFFFF)
val inversePrimaryLightHighContrast = Color(0xFFFFE8D3)
val surfaceDimLightHighContrast = Color(0xFFE5D8CC)
val surfaceBrightLightHighContrast = Color(0xFFFFF8F4)
val surfaceContainerLowestLightHighContrast = Color(0xFFFFFFFF)
val surfaceContainerLowLightHighContrast = Color(0xFFFFF1E6)
val surfaceContainerLightHighContrast = Color(0xFFFAEBE0)
val surfaceContainerHighLightHighContrast = Color(0xFFF4E6DA)
val surfaceContainerHighestLightHighContrast = Color(0xFFEEE0D5)

val primaryDark = Color(0xFFF9BA72)
val onPrimaryDark = Color(0xFF482A00)
val primaryContainerDark = Color(0xFF673D00)
val onPrimaryContainerDark = Color(0xFFFFDDBA)
val secondaryDark = Color(0xFFE0C1A3)
val onSecondaryDark = Color(0xFF3F2D17)
val secondaryContainerDark = Color(0xFF58432B)
val onSecondaryContainerDark = Color(0xFFFDDDBD)
val tertiaryDark = Color(0xFFBDCD9D)
val onTertiaryDark = Color(0xFF283412)
val tertiaryContainerDark = Color(0xFF3E4B26)
val onTertiaryContainerDark = Color(0xFFD9E9B7)
val errorDark = Color(0xFFFFB4AB)
val onErrorDark = Color(0xFF690005)
val errorContainerDark = Color(0xFF93000A)
val onErrorContainerDark = Color(0xFFFFDAD6)
val backgroundDark = Color(0xFF18120C)
val onBackgroundDark = Color(0xFFEEE0D5)
val surfaceDark = Color(0xFF18120C)
val onSurfaceDark = Color(0xFFEEE0D5)
val surfaceVariantDark = Color(0xFF50453A)
val onSurfaceVariantDark = Color(0xFFD4C4B5)
val outlineDark = Color(0xFF9D8E81)
val outlineVariantDark = Color(0xFF50453A)
val scrimDark = Color(0xFF000000)
val inverseSurfaceDark = Color(0xFFEEE0D5)
val inverseOnSurfaceDark = Color(0xFF372F27)
val inversePrimaryDark = Color(0xFF835415)
val surfaceDimDark = Color(0xFF18120C)
val surfaceBrightDark = Color(0xFF403830)
val surfaceContainerLowestDark = Color(0xFF130D07)
val surfaceContainerLowDark = Color(0xFF211A13)
val surfaceContainerDark = Color(0xFF251E17)
val surfaceContainerHighDark = Color(0xFF302921)
val surfaceContainerHighestDark = Color(0xFF3B332C)

val primaryDarkMediumContrast = Color(0xFFFEBE76)
val onPrimaryDarkMediumContrast = Color(0xFF241200)
val primaryContainerDarkMediumContrast = Color(0xFFBD8543)
val onPrimaryContainerDarkMediumContrast = Color(0xFF000000)
val secondaryDarkMediumContrast = Color(0xFFE4C5A7)
val onSecondaryDarkMediumContrast = Color(0xFF221302)
val secondaryContainerDarkMediumContrast = Color(0xFFA78C70)
val onSecondaryContainerDarkMediumContrast = Color(0xFF000000)
val tertiaryDarkMediumContrast = Color(0xFFC1D1A1)
val onTertiaryDarkMediumContrast = Color(0xFF0F1900)
val tertiaryContainerDarkMediumContrast = Color(0xFF87966A)
val onTertiaryContainerDarkMediumContrast = Color(0xFF000000)
val errorDarkMediumContrast = Color(0xFFFFBAB1)
val onErrorDarkMediumContrast = Color(0xFF370001)
val errorContainerDarkMediumContrast = Color(0xFFFF5449)
val onErrorContainerDarkMediumContrast = Color(0xFF000000)
val backgroundDarkMediumContrast = Color(0xFF18120C)
val onBackgroundDarkMediumContrast = Color(0xFFEEE0D5)
val surfaceDarkMediumContrast = Color(0xFF18120C)
val onSurfaceDarkMediumContrast = Color(0xFFFFFAF8)
val surfaceVariantDarkMediumContrast = Color(0xFF50453A)
val onSurfaceVariantDarkMediumContrast = Color(0xFFD9C8B9)
val outlineDarkMediumContrast = Color(0xFFAFA092)
val outlineVariantDarkMediumContrast = Color(0xFF8F8174)
val scrimDarkMediumContrast = Color(0xFF000000)
val inverseSurfaceDarkMediumContrast = Color(0xFFEEE0D5)
val inverseOnSurfaceDarkMediumContrast = Color(0xFF302921)
val inversePrimaryDarkMediumContrast = Color(0xFF683E00)
val surfaceDimDarkMediumContrast = Color(0xFF18120C)
val surfaceBrightDarkMediumContrast = Color(0xFF403830)
val surfaceContainerLowestDarkMediumContrast = Color(0xFF130D07)
val surfaceContainerLowDarkMediumContrast = Color(0xFF211A13)
val surfaceContainerDarkMediumContrast = Color(0xFF251E17)
val surfaceContainerHighDarkMediumContrast = Color(0xFF302921)
val surfaceContainerHighestDarkMediumContrast = Color(0xFF3B332C)

val primaryDarkHighContrast = Color(0xFFFFFAF8)
val onPrimaryDarkHighContrast = Color(0xFF000000)
val primaryContainerDarkHighContrast = Color(0xFFFEBE76)
val onPrimaryContainerDarkHighContrast = Color(0xFF000000)
val secondaryDarkHighContrast = Color(0xFFFFFAF8)
val onSecondaryDarkHighContrast = Color(0xFF000000)
val secondaryContainerDarkHighContrast = Color(0xFFE4C5A7)
val onSecondaryContainerDarkHighContrast = Color(0xFF000000)
val tertiaryDarkHighContrast = Color(0xFFF5FFDC)
val onTertiaryDarkHighContrast = Color(0xFF000000)
val tertiaryContainerDarkHighContrast = Color(0xFFC1D1A1)
val onTertiaryContainerDarkHighContrast = Color(0xFF000000)
val errorDarkHighContrast = Color(0xFFFFF9F9)
val onErrorDarkHighContrast = Color(0xFF000000)
val errorContainerDarkHighContrast = Color(0xFFFFBAB1)
val onErrorContainerDarkHighContrast = Color(0xFF000000)
val backgroundDarkHighContrast = Color(0xFF18120C)
val onBackgroundDarkHighContrast = Color(0xFFEEE0D5)
val surfaceDarkHighContrast = Color(0xFF18120C)
val onSurfaceDarkHighContrast = Color(0xFFFFFFFF)
val surfaceVariantDarkHighContrast = Color(0xFF50453A)
val onSurfaceVariantDarkHighContrast = Color(0xFFFFFAF8)
val outlineDarkHighContrast = Color(0xFFD9C8B9)
val outlineVariantDarkHighContrast = Color(0xFFD9C8B9)
val scrimDarkHighContrast = Color(0xFF000000)
val inverseSurfaceDarkHighContrast = Color(0xFFEEE0D5)
val inverseOnSurfaceDarkHighContrast = Color(0xFF000000)
val inversePrimaryDarkHighContrast = Color(0xFF3F2400)
val surfaceDimDarkHighContrast = Color(0xFF18120C)
val surfaceBrightDarkHighContrast = Color(0xFF403830)
val surfaceContainerLowestDarkHighContrast = Color(0xFF130D07)
val surfaceContainerLowDarkHighContrast = Color(0xFF211A13)
val surfaceContainerDarkHighContrast = Color(0xFF251E17)
val surfaceContainerHighDarkHighContrast = Color(0xFF302921)
val surfaceContainerHighestDarkHighContrast = Color(0xFF3B332C)

private val lightScheme = lightColorScheme(
    primary = primaryLight,
    onPrimary = onPrimaryLight,
    primaryContainer = primaryContainerLight,
    onPrimaryContainer = onPrimaryContainerLight,
    secondary = secondaryLight,
    onSecondary = onSecondaryLight,
    secondaryContainer = secondaryContainerLight,
    onSecondaryContainer = onSecondaryContainerLight,
    tertiary = tertiaryLight,
    onTertiary = onTertiaryLight,
    tertiaryContainer = tertiaryContainerLight,
    onTertiaryContainer = onTertiaryContainerLight,
    error = errorLight,
    onError = onErrorLight,
    errorContainer = errorContainerLight,
    onErrorContainer = onErrorContainerLight,
    background = backgroundLight,
    onBackground = onBackgroundLight,
    surface = surfaceLight,
    onSurface = onSurfaceLight,
    surfaceVariant = surfaceVariantLight,
    onSurfaceVariant = onSurfaceVariantLight,
    outline = outlineLight,
    outlineVariant = outlineVariantLight,
    scrim = scrimLight,
    inverseSurface = inverseSurfaceLight,
    inverseOnSurface = inverseOnSurfaceLight,
    inversePrimary = inversePrimaryLight,
    surfaceDim = surfaceDimLight,
    surfaceBright = surfaceBrightLight,
    surfaceContainerLowest = surfaceContainerLowestLight,
    surfaceContainerLow = surfaceContainerLowLight,
    surfaceContainer = surfaceContainerLight,
    surfaceContainerHigh = surfaceContainerHighLight,
    surfaceContainerHighest = surfaceContainerHighestLight,
)

private val darkScheme = darkColorScheme(
    primary = primaryDark,
    onPrimary = onPrimaryDark,
    primaryContainer = primaryContainerDark,
    onPrimaryContainer = onPrimaryContainerDark,
    secondary = secondaryDark,
    onSecondary = onSecondaryDark,
    secondaryContainer = secondaryContainerDark,
    onSecondaryContainer = onSecondaryContainerDark,
    tertiary = tertiaryDark,
    onTertiary = onTertiaryDark,
    tertiaryContainer = tertiaryContainerDark,
    onTertiaryContainer = onTertiaryContainerDark,
    error = errorDark,
    onError = onErrorDark,
    errorContainer = errorContainerDark,
    onErrorContainer = onErrorContainerDark,
    background = backgroundDark,
    onBackground = onBackgroundDark,
    surface = surfaceDark,
    onSurface = onSurfaceDark,
    surfaceVariant = surfaceVariantDark,
    onSurfaceVariant = onSurfaceVariantDark,
    outline = outlineDark,
    outlineVariant = outlineVariantDark,
    scrim = scrimDark,
    inverseSurface = inverseSurfaceDark,
    inverseOnSurface = inverseOnSurfaceDark,
    inversePrimary = inversePrimaryDark,
    surfaceDim = surfaceDimDark,
    surfaceBright = surfaceBrightDark,
    surfaceContainerLowest = surfaceContainerLowestDark,
    surfaceContainerLow = surfaceContainerLowDark,
    surfaceContainer = surfaceContainerDark,
    surfaceContainerHigh = surfaceContainerHighDark,
    surfaceContainerHighest = surfaceContainerHighestDark,
)

private val mediumContrastLightColorScheme = lightColorScheme(
    primary = primaryLightMediumContrast,
    onPrimary = onPrimaryLightMediumContrast,
    primaryContainer = primaryContainerLightMediumContrast,
    onPrimaryContainer = onPrimaryContainerLightMediumContrast,
    secondary = secondaryLightMediumContrast,
    onSecondary = onSecondaryLightMediumContrast,
    secondaryContainer = secondaryContainerLightMediumContrast,
    onSecondaryContainer = onSecondaryContainerLightMediumContrast,
    tertiary = tertiaryLightMediumContrast,
    onTertiary = onTertiaryLightMediumContrast,
    tertiaryContainer = tertiaryContainerLightMediumContrast,
    onTertiaryContainer = onTertiaryContainerLightMediumContrast,
    error = errorLightMediumContrast,
    onError = onErrorLightMediumContrast,
    errorContainer = errorContainerLightMediumContrast,
    onErrorContainer = onErrorContainerLightMediumContrast,
    background = backgroundLightMediumContrast,
    onBackground = onBackgroundLightMediumContrast,
    surface = surfaceLightMediumContrast,
    onSurface = onSurfaceLightMediumContrast,
    surfaceVariant = surfaceVariantLightMediumContrast,
    onSurfaceVariant = onSurfaceVariantLightMediumContrast,
    outline = outlineLightMediumContrast,
    outlineVariant = outlineVariantLightMediumContrast,
    scrim = scrimLightMediumContrast,
    inverseSurface = inverseSurfaceLightMediumContrast,
    inverseOnSurface = inverseOnSurfaceLightMediumContrast,
    inversePrimary = inversePrimaryLightMediumContrast,
    surfaceDim = surfaceDimLightMediumContrast,
    surfaceBright = surfaceBrightLightMediumContrast,
    surfaceContainerLowest = surfaceContainerLowestLightMediumContrast,
    surfaceContainerLow = surfaceContainerLowLightMediumContrast,
    surfaceContainer = surfaceContainerLightMediumContrast,
    surfaceContainerHigh = surfaceContainerHighLightMediumContrast,
    surfaceContainerHighest = surfaceContainerHighestLightMediumContrast,
)

private val highContrastLightColorScheme = lightColorScheme(
    primary = primaryLightHighContrast,
    onPrimary = onPrimaryLightHighContrast,
    primaryContainer = primaryContainerLightHighContrast,
    onPrimaryContainer = onPrimaryContainerLightHighContrast,
    secondary = secondaryLightHighContrast,
    onSecondary = onSecondaryLightHighContrast,
    secondaryContainer = secondaryContainerLightHighContrast,
    onSecondaryContainer = onSecondaryContainerLightHighContrast,
    tertiary = tertiaryLightHighContrast,
    onTertiary = onTertiaryLightHighContrast,
    tertiaryContainer = tertiaryContainerLightHighContrast,
    onTertiaryContainer = onTertiaryContainerLightHighContrast,
    error = errorLightHighContrast,
    onError = onErrorLightHighContrast,
    errorContainer = errorContainerLightHighContrast,
    onErrorContainer = onErrorContainerLightHighContrast,
    background = backgroundLightHighContrast,
    onBackground = onBackgroundLightHighContrast,
    surface = surfaceLightHighContrast,
    onSurface = onSurfaceLightHighContrast,
    surfaceVariant = surfaceVariantLightHighContrast,
    onSurfaceVariant = onSurfaceVariantLightHighContrast,
    outline = outlineLightHighContrast,
    outlineVariant = outlineVariantLightHighContrast,
    scrim = scrimLightHighContrast,
    inverseSurface = inverseSurfaceLightHighContrast,
    inverseOnSurface = inverseOnSurfaceLightHighContrast,
    inversePrimary = inversePrimaryLightHighContrast,
    surfaceDim = surfaceDimLightHighContrast,
    surfaceBright = surfaceBrightLightHighContrast,
    surfaceContainerLowest = surfaceContainerLowestLightHighContrast,
    surfaceContainerLow = surfaceContainerLowLightHighContrast,
    surfaceContainer = surfaceContainerLightHighContrast,
    surfaceContainerHigh = surfaceContainerHighLightHighContrast,
    surfaceContainerHighest = surfaceContainerHighestLightHighContrast,
)

private val mediumContrastDarkColorScheme = darkColorScheme(
    primary = primaryDarkMediumContrast,
    onPrimary = onPrimaryDarkMediumContrast,
    primaryContainer = primaryContainerDarkMediumContrast,
    onPrimaryContainer = onPrimaryContainerDarkMediumContrast,
    secondary = secondaryDarkMediumContrast,
    onSecondary = onSecondaryDarkMediumContrast,
    secondaryContainer = secondaryContainerDarkMediumContrast,
    onSecondaryContainer = onSecondaryContainerDarkMediumContrast,
    tertiary = tertiaryDarkMediumContrast,
    onTertiary = onTertiaryDarkMediumContrast,
    tertiaryContainer = tertiaryContainerDarkMediumContrast,
    onTertiaryContainer = onTertiaryContainerDarkMediumContrast,
    error = errorDarkMediumContrast,
    onError = onErrorDarkMediumContrast,
    errorContainer = errorContainerDarkMediumContrast,
    onErrorContainer = onErrorContainerDarkMediumContrast,
    background = backgroundDarkMediumContrast,
    onBackground = onBackgroundDarkMediumContrast,
    surface = surfaceDarkMediumContrast,
    onSurface = onSurfaceDarkMediumContrast,
    surfaceVariant = surfaceVariantDarkMediumContrast,
    onSurfaceVariant = onSurfaceVariantDarkMediumContrast,
    outline = outlineDarkMediumContrast,
    outlineVariant = outlineVariantDarkMediumContrast,
    scrim = scrimDarkMediumContrast,
    inverseSurface = inverseSurfaceDarkMediumContrast,
    inverseOnSurface = inverseOnSurfaceDarkMediumContrast,
    inversePrimary = inversePrimaryDarkMediumContrast,
    surfaceDim = surfaceDimDarkMediumContrast,
    surfaceBright = surfaceBrightDarkMediumContrast,
    surfaceContainerLowest = surfaceContainerLowestDarkMediumContrast,
    surfaceContainerLow = surfaceContainerLowDarkMediumContrast,
    surfaceContainer = surfaceContainerDarkMediumContrast,
    surfaceContainerHigh = surfaceContainerHighDarkMediumContrast,
    surfaceContainerHighest = surfaceContainerHighestDarkMediumContrast,
)

private val highContrastDarkColorScheme = darkColorScheme(
    primary = primaryDarkHighContrast,
    onPrimary = onPrimaryDarkHighContrast,
    primaryContainer = primaryContainerDarkHighContrast,
    onPrimaryContainer = onPrimaryContainerDarkHighContrast,
    secondary = secondaryDarkHighContrast,
    onSecondary = onSecondaryDarkHighContrast,
    secondaryContainer = secondaryContainerDarkHighContrast,
    onSecondaryContainer = onSecondaryContainerDarkHighContrast,
    tertiary = tertiaryDarkHighContrast,
    onTertiary = onTertiaryDarkHighContrast,
    tertiaryContainer = tertiaryContainerDarkHighContrast,
    onTertiaryContainer = onTertiaryContainerDarkHighContrast,
    error = errorDarkHighContrast,
    onError = onErrorDarkHighContrast,
    errorContainer = errorContainerDarkHighContrast,
    onErrorContainer = onErrorContainerDarkHighContrast,
    background = backgroundDarkHighContrast,
    onBackground = onBackgroundDarkHighContrast,
    surface = surfaceDarkHighContrast,
    onSurface = onSurfaceDarkHighContrast,
    surfaceVariant = surfaceVariantDarkHighContrast,
    onSurfaceVariant = onSurfaceVariantDarkHighContrast,
    outline = outlineDarkHighContrast,
    outlineVariant = outlineVariantDarkHighContrast,
    scrim = scrimDarkHighContrast,
    inverseSurface = inverseSurfaceDarkHighContrast,
    inverseOnSurface = inverseOnSurfaceDarkHighContrast,
    inversePrimary = inversePrimaryDarkHighContrast,
    surfaceDim = surfaceDimDarkHighContrast,
    surfaceBright = surfaceBrightDarkHighContrast,
    surfaceContainerLowest = surfaceContainerLowestDarkHighContrast,
    surfaceContainerLow = surfaceContainerLowDarkHighContrast,
    surfaceContainer = surfaceContainerDarkHighContrast,
    surfaceContainerHigh = surfaceContainerHighDarkHighContrast,
    surfaceContainerHighest = surfaceContainerHighestDarkHighContrast,
)

@Composable
fun navigatorColorScheme(dynamicColor: Boolean, darkTheme: Boolean): ColorScheme {
    return when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> darkScheme
        else -> lightScheme
    }
}

fun defaultNavigatorColor() = lightScheme
