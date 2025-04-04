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

import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import kotlin.coroutines.CoroutineContext

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SuspendLazyTests {
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun should_produce_value() = runTest {
        val lazyValue = SuspendLazy { delay(1_000); 123 }
        assertEquals(123, lazyValue())
        assertEquals(1_000, currentTime)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun should_not_recalculate_value() = runTest {
        var next = 1
        val lazyValue = SuspendLazy { delay(1_000); next++ }
        assertEquals(1, lazyValue())
        assertEquals(1, lazyValue())
        assertEquals(1, lazyValue())
        assertEquals(1, lazyValue())
        assertEquals(1_000, currentTime)
    }

    @Test
    fun should_not_calculate_value_multiple_times_when_multiple_coroutines_access_it() = runTest {
        var calculatedTimes = 0
        val lazyValue = SuspendLazy { delay(1_000); calculatedTimes++ }
        coroutineScope {
            repeat(10_000) {
                launch {
                    lazyValue()
                }
            }
        }
        assertEquals(1, calculatedTimes)
    }

    @Test
    fun should_try_again_when_failure_during_value_initialization() = runTest {
        var next = 0
        val lazyValue = SuspendLazy {
            val v = next++
            if (v < 2) throw Error()
            v
        }
        assertTrue(runCatching { lazyValue() }.isFailure)
        assertTrue(runCatching { lazyValue() }.isFailure)
        assertEquals(2, lazyValue())
        assertEquals(2, lazyValue())
        assertEquals(2, lazyValue())
    }

    @Test
    fun should_use_context_of_the_first_caller() = runTest {
        var ctx: CoroutineContext? = null
        val lazyValue = SuspendLazy {
            ctx = currentCoroutineContext()
            123
        }
        val name1 = CoroutineName("ABC")
        withContext(name1) {
            lazyValue()
        }
        assertEquals(name1, ctx?.get(CoroutineName))
        val name2 = CoroutineName("DEF")
        withContext(name2) {
            lazyValue()
        }
        assertEquals(name1, ctx?.get(CoroutineName))
    }
}