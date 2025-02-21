package org.giste.navigator.features.map.data

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import org.giste.navigator.features.map.domain.MapRepository
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class MapModule {
    @Singleton
    @Provides
    fun provideMapRepository(
        @ApplicationContext context: Context
    ): MapRepository = LocalMapRepository(context)
}