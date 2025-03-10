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

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.click
import androidx.compose.ui.test.hasProgressBarRangeInfo
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import de.mannodermaus.junit5.compose.createComposeExtension
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.giste.navigator.features.settings.domain.Settings
import org.giste.navigator.ui.NavigatorDialogTags.ACCEPT_BUTTON
import org.giste.navigator.ui.SetNumberDialogTags.KEY_DELETE
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.RegisterExtension

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SettingsInstrumentedTests {
    @OptIn(ExperimentalTestApi::class)
    @RegisterExtension
    @JvmField
    val extension = createComposeExtension()

    private val viewModel = mockk<SettingsViewModel>()

    @BeforeEach
    fun beforeEach() {
        clearAllMocks()
        every { viewModel.settingsState } returns MutableStateFlow(Settings()).asStateFlow()
        every { viewModel.onAction(any()) } returns Unit
    }

    @Test
    fun displays_settings_correctly() {
        extension.use {
            setContent { SettingsScreen(settingsViewModel =  viewModel, navigateBack = {}) }

            waitUntilExactlyOneExists(hasText("1.000"))
            waitUntilExactlyOneExists(hasText("10"))
            waitUntilExactlyOneExists(
                hasProgressBarRangeInfo(
                    ProgressBarRangeInfo(
                        current = 19f,
                        range = 15f..20f,
                        steps = 4,
                    )
                )
            )
        }
    }

    @Test
    fun saves_zoom_level_when_changed() {
        extension.use {
            setContent { SettingsScreen(settingsViewModel =  viewModel, navigateBack = {}) }

            onNode(hasProgressBarRangeInfo(ProgressBarRangeInfo(19f, 15f..20f, 4)))
                .performTouchInput { this.click(Offset(this.centerX - 1f, this.centerY)) }

            verify { viewModel.onAction(SettingsViewModel.UiAction.OnMapZoomLevelChange(17)) }
        }
    }

    @Test
    fun saves_min_distance_when_changed() {
        extension.use {
            setContent { SettingsScreen(settingsViewModel =  viewModel, navigateBack = {}) }

            waitUntilExactlyOneExists(hasText("10"))
            onNodeWithText("10").performClick()

            waitUntilExactlyOneExists(hasTestTag(KEY_DELETE))

            onNodeWithTag(KEY_DELETE).performClick()
            onNodeWithTag(ACCEPT_BUTTON).performClick()
        }

        verify { viewModel.onAction(SettingsViewModel.UiAction.OnLocationMinDistanceChange(1)) }
    }

    @Test
    fun saves_min_time_when_changed() {
        extension.use {
            setContent { SettingsScreen(settingsViewModel =  viewModel, navigateBack = {}) }

            waitUntilExactlyOneExists(hasText("1.000"))
            onNodeWithText("1.000").performClick()

            waitUntilExactlyOneExists(hasTestTag(KEY_DELETE))

            onNodeWithTag(KEY_DELETE).performClick()
            onNodeWithTag(ACCEPT_BUTTON).performClick()
        }

        verify { viewModel.onAction(SettingsViewModel.UiAction.OnLocationMinTimeChange(100)) }
    }
}