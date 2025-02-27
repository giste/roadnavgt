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

import android.icu.text.DecimalFormatSymbols
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import de.mannodermaus.junit5.compose.createComposeExtension
import org.giste.navigator.ui.SetNumberDialogTags.ACCEPT_BUTTON
import org.giste.navigator.ui.SetNumberDialogTags.KEY_DELETE
import org.giste.navigator.ui.SetNumberDialogTags.TITLE
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.RegisterExtension
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.junit.jupiter.params.provider.ValueSource
import java.util.Locale
import java.util.stream.Stream

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SetNumberDialogInstrumentedTests {
    @OptIn(ExperimentalTestApi::class)
    @RegisterExtension
    @JvmField
    val extension = createComposeExtension()

    @Test
    fun paramsAreCorrectlySet() {
        extension.use {
            setContent { SetTestNumberDialog() }

            onNodeWithTag(TITLE).assertTextEquals("Title")
            onNodeWithTag(SetNumberDialogTags.DESCRIPTION).assertTextEquals("Description")
            onNodeWithTag(SetNumberDialogTags.NUMBER_FIELD).assertTextEquals("0.00")
        }
    }

    @ParameterizedTest
    @ValueSource(strings = ["0", "1", "2", "3", "4", "5", "6", "7", "8", "9"])
    fun numberKeyWorks(key: String) {
        extension.use {
            setContent { SetTestNumberDialog() }

            onNodeWithText(key).performClick()
            onNodeWithTag(SetNumberDialogTags.NUMBER_FIELD).assertTextEquals("0.0$key")
        }
    }

    @Test
    fun deleteKeyWorks(){
        extension.use {
            setContent { SetTestNumberDialog(number = 1) }

            onNodeWithTag(KEY_DELETE).performClick()
            onNodeWithTag(SetNumberDialogTags.NUMBER_FIELD).assertTextEquals("0.00")
        }
    }

    @ParameterizedTest
    @MethodSource("provideTestNumbers")
    fun numbersAreCorrectlyFormatted(testNumber: TestNumber) {
        extension.use {
            setContent { SetTestNumberDialog() }

            testNumber.keys.listIterator().forEach {
                onNodeWithText(it).performClick()
            }
            onNodeWithTag(SetNumberDialogTags.NUMBER_FIELD)
                .assertTextEquals(testNumber.expectedFormat)
        }
    }

    private fun provideTestNumbers(): Stream<TestNumber> = Stream.of(
        TestNumber(listOf("1"), "0.01"),
        TestNumber(listOf("1", "2"), "0.12"),
        TestNumber(listOf("1", "2", "3"), "1.23"),
        TestNumber(listOf("1", "2", "3", "4"), "12.34"),
        TestNumber(listOf("1", "2", "3", "4", "5"), "123.45"),
        TestNumber(listOf("1", "2", "3", "4", "5", "6"), "1,234.56"),
    )

    class TestNumber(
        val keys: List<String>,
        val expectedFormat: String,
    )

    @Test
    fun dialogReturnsCorrectNumber() {
        var actualValue = 0
        val show = mutableStateOf(true)

        extension.use {
            setContent {
                SetTestNumberDialog(
                    showDialog = show,
                    onAccept = {
                        actualValue = it
                        show.value = false
                    },
                )
            }
            for (i in 1..6) {
                onNodeWithText(i.toString()).performClick()
            }
            onNodeWithTag(ACCEPT_BUTTON).performClick()
        }

        assertEquals(123456, actualValue)
        assertFalse(show.value)
    }

    @Composable
    fun SetTestNumberDialog(
        showDialog: MutableState<Boolean> = mutableStateOf(true),
        title: String = "Title",
        description: String = "Description",
        number: Int = 0,
        numberOfIntegerDigits: Int = 4,
        numberOfDecimalDigits: Int = 2,
        onAccept: (Int) -> Unit = { },
        decimalFormatSymbols: DecimalFormatSymbols = DecimalFormatSymbols(Locale("en")),
    ) {
        SetNumberDialog(
            showDialog = showDialog,
            title = title,
            description = description,
            number = number,
            numberOfIntegerDigits = numberOfIntegerDigits,
            numberOfDecimalDigits = numberOfDecimalDigits,
            onAccept = onAccept,
            decimalFormatSymbols = decimalFormatSymbols
        )
    }
}