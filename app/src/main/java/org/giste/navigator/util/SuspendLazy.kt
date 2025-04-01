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

package org.giste.navigator.util

import android.util.Log
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

private const val TAG = "SuspendLazy"

class SuspendLazy<T: Any>(private val initializer: suspend () -> T) {
    @Volatile
    private lateinit var value: T
    private val mutex = Mutex()

    suspend operator fun invoke(): T {
        return mutex.withLock {
            if (!this::value.isInitialized) {
                Log.d(TAG, "Initializing...")
                value = initializer()
                Log.d(TAG, "Initialized $value")
            }
            value
        }
    }
}
