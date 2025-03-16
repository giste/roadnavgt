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

package org.giste.navigator.features.roadbook.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import org.giste.navigator.StateDatastore
import org.giste.navigator.features.roadbook.domain.RoadbookRepository
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class RoadbookModule {
    @Singleton
    @Provides
    fun provideRoadbookDatasource(
        @ApplicationContext context: Context,
    ): RoadbookDatasource = PdfRendererRoadbookDatasource(context)

    @Singleton
    @Provides
    fun provideRoadbookRepository(
        @StateDatastore dataStore: DataStore<Preferences>,
        roadbookDatasource: RoadbookDatasource,
    ): RoadbookRepository = DataStoreRoadbookRepository(dataStore, roadbookDatasource)
}