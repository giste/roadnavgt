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

import androidx.compose.ui.input.key.Key
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performKeyInput
import androidx.compose.ui.test.pressKey
import androidx.core.graphics.createBitmap
import androidx.paging.Pager
import androidx.paging.PagingConfig
import de.mannodermaus.junit5.compose.createComposeExtension
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.giste.navigator.features.location.domain.Location
import org.giste.navigator.features.roadbook.data.RoadbookDatasource
import org.giste.navigator.features.roadbook.data.RoadbookPagingSource
import org.giste.navigator.features.roadbook.domain.Roadbook
import org.giste.navigator.features.roadbook.domain.RoadbookPage
import org.giste.navigator.features.roadbook.domain.Scroll
import org.giste.navigator.features.roadbook.ui.RoadbookTags.ROADBOOK
import org.giste.navigator.features.roadbook.ui.RoadbookTags.ROADBOOK_PAGE_
import org.giste.navigator.features.settings.domain.Settings
import org.giste.navigator.features.trip.domain.Trip
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.RegisterExtension

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class NavigatorScreenRoadbookInstrumentedTests {
    @OptIn(ExperimentalTestApi::class)
    @RegisterExtension
    @JvmField
    val extension = createComposeExtension()

    private val viewModel = mockk<NavigatorViewModel>()

    private val locationFlow = MutableStateFlow<Location?>(null)
    private val mapFlow = MutableStateFlow<List<String>>(emptyList())
    private val roadbookFlow = MutableStateFlow<Roadbook>(Roadbook.NotLoaded)
    private val settingsFlow = MutableStateFlow(Settings())
    private val tripFlow = MutableStateFlow(Trip())

    @BeforeEach
    fun beforeEach() {
        clearAllMocks()
        locationFlow.update { null }
        mapFlow.update { emptyList() }
        roadbookFlow.update { Roadbook.NotLoaded }
        settingsFlow.update { Settings() }
        tripFlow.update { Trip() }
        every { viewModel.locationState } returns locationFlow.asStateFlow()
        every { viewModel.mapSourceState } returns mapFlow.asStateFlow()
        every { viewModel.roadbookState } returns roadbookFlow.asStateFlow()
        every { viewModel.settingsState } returns settingsFlow.asStateFlow()
        every { viewModel.tripState } returns tripFlow.asStateFlow()
        every { viewModel.onAction(any()) } returns Unit
    }

    @Test
    fun roadbook_is_loaded() {
        val roadbookDatasource = FakeRoadbookDatasource(10, 100)
        val pager = Pager(config = PagingConfig(pageSize = 5), initialKey = 0) {
            RoadbookPagingSource(roadbookDatasource)
        }.flow

        extension.use {
            setContent {
                NavigatorScreen(
                    viewModel = viewModel,
                    navigateToSettings = {},
                    navigateToMapManager = {}
                )
            }

            roadbookFlow.update { Roadbook.Loaded(pager, Scroll()) }
            waitForIdle()

            onNodeWithTag("${ROADBOOK_PAGE_}0").isDisplayed()
        }
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun up_key_moves_roadbook() {
        val roadbookDatasource = FakeRoadbookDatasource(5, 1000)
        val pager = Pager(config = PagingConfig(pageSize = 5), initialKey = 0) {
            RoadbookPagingSource(roadbookDatasource)
        }.flow
        every { viewModel.onAction(any()) } returns Unit

        extension.use {
            setContent {
                NavigatorScreen(
                    viewModel = viewModel,
                    navigateToSettings = {},
                    navigateToMapManager = {}
                )
            }

            roadbookFlow.update { Roadbook.Loaded(pager, Scroll()) }
            settingsFlow.update { Settings(pixelsToMoveRoadbook = 100) }
            waitForIdle()
            onNodeWithTag(ROADBOOK).performKeyInput {
                pressKey(Key.DirectionUp)
            }
            waitForIdle()
        }

        verify { viewModel.onAction(NavigatorViewModel.UiAction.SaveScroll(0, 100)) }
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun down_key_moves_roadbook() {
        val roadbookDatasource = FakeRoadbookDatasource(5, 1000)
        val pager = Pager(config = PagingConfig(pageSize = 5), initialKey = 0) {
            RoadbookPagingSource(roadbookDatasource)
        }.flow
        every { viewModel.onAction(any()) } returns Unit

        extension.use {
            setContent {
                NavigatorScreen(
                    viewModel = viewModel,
                    navigateToSettings = {},
                    navigateToMapManager = {}
                )
            }

            roadbookFlow.update { Roadbook.Loaded(pager, Scroll(0, 200)) }
            settingsFlow.update { Settings(pixelsToMoveRoadbook = 100) }
            waitForIdle()
            onNodeWithTag(ROADBOOK).performKeyInput {
                pressKey(Key.DirectionDown)
            }
            waitForIdle()
        }

        verify { viewModel.onAction(NavigatorViewModel.UiAction.SaveScroll(0, 100)) }
    }

    class FakeRoadbookDatasource(
        private val numberOfPages: Int,
        private val pageSize: Int,
    ) : RoadbookDatasource {
        override suspend fun loadRoadbook(uri: String) {}

        override fun getPageCount(): Int = numberOfPages

        override suspend fun loadPages(
            startPosition: Int,
            loadSize: Int
        ): List<RoadbookPage> {
            val pages: MutableList<RoadbookPage> = mutableListOf()

            (startPosition..(startPosition + loadSize - 1)).forEach { key ->
                val page = RoadbookPage(
                    index = key,
                    page = createBitmap(pageSize, pageSize)
                )
                pages.add(page)
            }

            return pages
        }
    }
}