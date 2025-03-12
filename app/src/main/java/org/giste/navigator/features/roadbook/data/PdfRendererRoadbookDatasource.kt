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
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.util.Log
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.giste.navigator.features.roadbook.domain.RoadbookPage
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import javax.inject.Inject
import androidx.core.net.toUri
import androidx.core.graphics.createBitmap
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

private const val TAG = "RoadbookDatasourcePdfRenderer"
private const val ROADBOOK_FILE = "roadbook.pdf"
private const val TARGET_DPI = 144
private const val DEFAULT_DPI = 72
private const val PDF_STARTING_PAGE_INDEX = 0

class PdfRendererRoadbookDatasource @Inject constructor(
    private val context: Context,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : RoadbookDatasource {
    private var renderer: PdfRenderer? = createRenderer()
    private val mutex = Mutex()

    override suspend fun loadRoadbook(uri: String) {
        withContext(dispatcher) {
            val roadbookUri = uri.toUri()
            val roadbookFile =
                File(context.filesDir, ROADBOOK_FILE)

            renderer?.close()
            if (roadbookFile.exists()) roadbookFile.delete()
            roadbookFile.createNewFile()

            val inputStream: InputStream = context.contentResolver.openInputStream(roadbookUri)
                ?: throw IllegalArgumentException("Invalid URI: $roadbookUri")
            val outputStream = FileOutputStream(roadbookFile)

            inputStream.copyTo(outputStream)
            outputStream.flush()
            inputStream.close()
            outputStream.close()

            renderer = createRenderer()

            Log.i(TAG, "Loaded roadbook: ${roadbookFile.path}")
        }
    }

    override fun getPageCount(): Int {
        return renderer?.pageCount ?: 0
    }

    override suspend fun loadPages(startPosition: Int, loadSize: Int): List<RoadbookPage> {
        if (getInternalUri() == Uri.EMPTY) return emptyList()

        val pages = mutableListOf<RoadbookPage>()

        withContext(dispatcher) {
            mutex.withLock {
                renderer?.let {
                    val start = startPosition.coerceAtLeast(PDF_STARTING_PAGE_INDEX)
                    val end = (startPosition + loadSize).coerceAtMost(it.pageCount)
                    for (index in start until end) {
                        Log.d(TAG, "Processing page $index")
                        it.openPage(index).use { page ->
                            val bitmap = drawBitmapLogic(page)
                            pages.add(RoadbookPage(index, bitmap))
                        }
                    }
                }
            }
        }

        return pages
    }

    /**
     * Draws the contents of a `PdfRenderer.Page` onto a `Bitmap`.
     *
     * @param page The `PdfRenderer.Page` to render.
     * @return The rendered `Bitmap`.
     */
    private fun drawBitmapLogic(page: PdfRenderer.Page): Bitmap {
        val bitmap = createBitmap(
            page.width * TARGET_DPI / DEFAULT_DPI,
            page.height * TARGET_DPI / DEFAULT_DPI,
        )

        Canvas(bitmap).apply {
            drawColor(Color.WHITE)
            drawBitmap(bitmap, 0f, 0f, null)
        }

        page.render(
            bitmap,
            null,
            null,
            PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY,
        )

        return bitmap
    }

    /**
     * Gets the URI of the roadbook in internal storage.
     *
     * @return URI of the roadbook or `Uri.EMPTY` if there is no roadbook loaded.
     */
    private fun getInternalUri(): Uri {
        val file = File(context.filesDir, ROADBOOK_FILE)

        return if (file.exists()) {
            Uri.fromFile(file)
        } else {
            Uri.EMPTY
        }
    }

    private fun createRenderer(): PdfRenderer? {
        return with(getInternalUri()) {
            if (this == Uri.EMPTY) {
                null
            } else {
                Log.d(TAG, "Creating renderer for $this")
                PdfRenderer(context.contentResolver.openFileDescriptor(this, "r")!!)
            }
        }
    }
}
