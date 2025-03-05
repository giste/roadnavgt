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

package org.giste.navigator.features.location.data

import android.content.Context
import android.location.LocationManager
import android.os.Looper
import androidx.core.content.ContextCompat.getSystemService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import org.giste.navigator.ApplicationScope
import org.giste.navigator.IoDispatcher
import org.giste.navigator.features.location.domain.LocationRepository
import org.giste.navigator.features.settings.domain.SettingsRepository
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class LocationModule {
    @Singleton
    @Provides
    fun provideLocationManager(
        @ApplicationContext context: Context,
    ) = getSystemService(context, LocationManager::class.java) as LocationManager

    @Singleton
    @Provides
    fun provideLocationRepository(
        locationManager: LocationManager,
        looper: Looper,
        settingsRepository: SettingsRepository,
        @ApplicationScope coroutineScope: CoroutineScope,
        @IoDispatcher dispatcher: CoroutineDispatcher,
    ): LocationRepository = ManagerLocationRepository(
        locationManager, looper, settingsRepository, coroutineScope, dispatcher
    )
}