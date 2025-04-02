package org.giste.navigator.features.map.data

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import org.giste.navigator.features.map.domain.MapRepository
import org.giste.navigator.features.map.domain.NewMapRepository
import java.nio.file.Paths
import javax.inject.Singleton
import kotlin.io.path.Path

@InstallIn(SingletonComponent::class)
@Module
class MapModule {
    @Singleton
    @Provides
    fun provideMapRepository(
        @ApplicationContext context: Context
    ): MapRepository = LocalMapRepository(context)

    @Singleton
    @Provides
    fun provideNewMapRepository(
        @ApplicationContext context: Context,
    ): NewMapRepository {
        return MapsforgeMapRepository(
            mapsDir = Path(context.filesDir.path).resolve("maps/"),
            remoteMapDatasource = RemoteMapDatasource(),
            localMapDatasource = LocalMapDatasource(),
        )
    }
}