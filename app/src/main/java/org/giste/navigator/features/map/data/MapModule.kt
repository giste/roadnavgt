package org.giste.navigator.features.map.data

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import org.giste.navigator.features.map.domain.MapRepository
import javax.inject.Singleton
import kotlin.io.path.Path

@InstallIn(SingletonComponent::class)
@Module
class MapModule {
    @Singleton
    @Provides
    fun provideNewMapRepository(
        @ApplicationContext context: Context,
    ): MapRepository {
        return MapsforgeMapRepository(
            mapsDir = Path(context.filesDir.path).resolve("maps/"),
            remoteMapDatasource = RemoteMapDatasource(),
            localMapDatasource = LocalMapDatasource(),
        )
    }
}