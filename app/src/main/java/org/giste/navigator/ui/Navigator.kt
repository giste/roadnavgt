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

import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.serialization.Serializable

@Composable
fun Navigator() {
    val navController = rememberNavController()

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Destinations.Navigator,
            modifier = Modifier
                .padding(innerPadding)
                // Consume this insets so that it's not applied again
                // when using safeDrawing in the hierarchy below
                .consumeWindowInsets(innerPadding)
        ) {
            composable<Destinations.Navigator> {
                NavigatorScreen(
                    viewModel = hiltViewModel<NavigatorViewModel>(),
                    navigateToSettings = {
                        navController.navigate(route = Destinations.Settings)
                    },
                    navigateToMapManager = {
                        navController.navigate(route = Destinations.MapManager)
                    }
                )
            }
            composable<Destinations.Settings> {
                SettingsScreen(
                    settingsViewModel = hiltViewModel<SettingsViewModel>(),
                    navigateBack = { navController.popBackStack() }
                )
            }
            composable<Destinations.MapManager> {
                MapManagerScreen(
                    mapManagerViewModel = hiltViewModel<MapManagerViewModel>(),
                    navigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}

sealed class Destinations {
    @Serializable
    data object Navigator : Destinations()
    @Serializable
    data object Settings : Destinations()
    @Serializable
    data object MapManager : Destinations()
}