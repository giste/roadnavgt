package org.giste.navigator.features.map.data

import android.content.Context
import android.util.Log
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.giste.navigator.features.map.domain.Map
import org.giste.navigator.features.map.domain.MapRepository
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

private const val TAG = "LocalMapRepository"
private const val MAPS_DIR = "maps"
private const val MAP_EXTENSION = "map"

class LocalMapRepository @Inject constructor(
    private val context: Context,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : MapRepository {
    companion object {
        val MAP_LIST = listOf<Map>(
            //"madrid.map",
        )
    }

    override suspend fun getMaps(): List<Map> {
        val maps = mutableListOf<Map>()

        withContext(dispatcher) {
            val mapsDir = File(context.filesDir, MAPS_DIR)

            copyMaps()
            mapsDir.walkTopDown().forEach {
                Log.d(TAG, "Evaluating file: ${it.path}")
                if (it.isFile && it.extension == MAP_EXTENSION) {
                    maps.add(Map(it.name, it.path))
                }
            }

            Log.d(TAG, "getMaps() = $maps")
        }

        return maps
    }

    private fun copyMap(mapName: String) {
        val filesDir = context.filesDir
        val mapsDir = File(filesDir, MAPS_DIR)
        val file = File(mapsDir, mapName)

        Log.d(TAG, "Copying map: ${file.path}")

        if (file.exists()) file.delete()
        file.createNewFile()

        val inputStream = context.assets.open("maps/$mapName")
        val outputStream = FileOutputStream(file)
        inputStream.copyTo(outputStream)
        outputStream.flush()
        inputStream.close()
        outputStream.close()

        mapExists(mapName)
    }

    private fun mapExists(mapName: String): Boolean {
        val filesDir = context.filesDir
        val mapsDir = File(filesDir, MAPS_DIR)
        val file = File(mapsDir, mapName)

        Log.d(TAG, "Checking map: ${file.path}; Exists: ${file.exists()}")

        return file.exists()
    }

    private fun copyMaps() {
        createMapsDir()

        MAP_LIST.forEach {
            if (!mapExists(it.path)) copyMap(it.path)
        }
    }

    private fun createMapsDir() {
        val filesDir = context.filesDir
        val mapsDir = File(filesDir, MAPS_DIR)

        Log.d(TAG, "Creating maps dir: ${mapsDir.path}")

        //if (!mapsDir.exists()) mapsDir.mkdirs()
        mapsDir.mkdirs()

        Log.d(TAG, "Maps dir exists: ${mapsDir.exists()}")
    }

}