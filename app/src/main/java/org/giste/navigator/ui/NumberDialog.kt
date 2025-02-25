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
import android.util.Log
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import org.giste.navigator.R
import org.giste.navigator.ui.theme.NavigatorTheme

private const val BACKSPACE = "<"

@Preview(
    name = "Tab Active 3 Landscape",
    showBackground = true,
    device = "spec:width=1920px,height=1200px,dpi=360, isRound=false, orientation=landscape"
)
@Preview(
    name = "Tab Active 3 Portrait",
    showBackground = true,
    device = "spec:width=1920px,height=1200px,dpi=360, isRound=false, orientation=portrait"
)
@Composable
private fun NumberDialogPreview() {
    NavigatorTheme(dynamicColor = true, darkTheme = true) {
        NumberDialog(
            showDialog = remember { mutableStateOf(true) },
            title = "Title",
            description = "Description of the requested number",
            text = "999,99",
            numberOfIntegerDigits = 4,
            numberOfDecimalDigits = 2,
            onAccept = { },
        )
    }
}

@Composable
fun NumberDialog(
    showDialog: MutableState<Boolean>,
    title: String,
    description: String,
    text: String,
    numberOfIntegerDigits: Int,
    numberOfDecimalDigits: Int,
    onAccept: (String) -> Unit,
    decimalFormatSymbols: DecimalFormatSymbols = DecimalFormatSymbols.getInstance(),
) {
    val thousandsSeparator by remember { mutableStateOf(decimalFormatSymbols.groupingSeparator.toString()) }
    val decimalSeparator by remember { mutableStateOf(decimalFormatSymbols.decimalSeparator) }
    val zeroDigit by remember { mutableStateOf(decimalFormatSymbols.zeroDigit.toString()) }

//    fun parseNumber(text: String): String {
//        val hasDecimalSeparator = text.find { it == decimalSeparator } != null
//        val decimalPart = if (hasDecimalSeparator) {
//            text.dropWhile { it != decimalSeparator }.drop(1)
//        } else {
//            ""
//        }
//        val intPart = text.take(text.length - decimalPart.length - 1)
//    }

    var number by rememberSaveable { mutableStateOf(text) }

    fun processKey(key: String) {
        val currentNumber = number.filter { it.isDigit() }

        Log.d("processKey", "Current number: $currentNumber, Key: $key")

        val newNumber = when (key) {
            BACKSPACE -> if (currentNumber.length == 1) zeroDigit else currentNumber.dropLast(1)

            else -> if (currentNumber.length < numberOfIntegerDigits + numberOfDecimalDigits) {
                if (currentNumber == zeroDigit) key else "$currentNumber$key"
            } else {
                currentNumber
            }
        }

        Log.d("processKey", "New number: $newNumber")

        val fractionalPart = newNumber.takeLast(numberOfDecimalDigits)
        val integerPart = if (newNumber.length > numberOfDecimalDigits) {
            newNumber.take(newNumber.length - numberOfDecimalDigits)
        } else {
            ""
        }.reversed()
            .chunked(3)
            .joinToString(thousandsSeparator)
            .reversed()

        Log.d("processKey", "Integer: $integerPart, Fractional: $fractionalPart")

        number = if (integerPart.isEmpty()) {
            fractionalPart
        } else {
            "$integerPart$decimalSeparator$fractionalPart"
        }

        Log.d("processKey", "Number: $number")
    }

    fun getBackgroundMask(): String {
        val integerPart = generateSequence { zeroDigit }
            .take(numberOfIntegerDigits)
            .joinToString("")
            .chunked(3)
            .joinToString(thousandsSeparator)
            .reversed()

        val fractionalPart = if (numberOfDecimalDigits == 0) {
            ""
        } else {
            StringBuilder()
                .append(decimalSeparator)
                .append(
                    generateSequence { zeroDigit }
                        .take(numberOfDecimalDigits)
                        .joinToString("")
                )
        }

        return "$integerPart$fractionalPart".dropLast(number.length)
    }

    Dialog(
        onDismissRequest = { showDialog.value = false },
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false,
        )
    ) {
        Surface {
            Column(
                modifier = Modifier
                    .width(
                        width = NavigatorTheme.dimensions.dialogWidth,
                    )
                    .wrapContentHeight()
            ) {
                DialogTitle(title)
                HorizontalDivider()
                Row(
                    modifier = Modifier
                        .height(IntrinsicSize.Min),
                ) {
                    Column(
                        modifier = Modifier
                            .weight(2f),
                    ) {
                        Text(
                            text = description,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(NavigatorTheme.dimensions.marginPadding),
                            overflow = TextOverflow.Clip,
                            softWrap = true,
                            style = NavigatorTheme.typography.bodyLarge,
                        )
                        NumberField(
                            backgroundMask = { getBackgroundMask() },
                            number = number,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(NavigatorTheme.dimensions.marginPadding),
                        )
                    }
                    VerticalDivider()
                    Column(
                        modifier = Modifier
                            .weight(1f),
                    ) {
                        NumberPad(
                            onClick = { key -> processKey(key) }
                        )
                    }
                }
                HorizontalDivider()
                DialogButtons(
                    onCancel = { showDialog.value = false },
                    onAccept = { onAccept(number) },
                )
            }
        }
    }
}

@Composable
fun DialogTitle(
    title: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = title,
        modifier = modifier
            .fillMaxWidth()
            .background(NavigatorTheme.colors.primary)
            .padding(NavigatorTheme.dimensions.marginPadding),
        color = NavigatorTheme.colors.onPrimary,
        style = NavigatorTheme.typography.titleLarge,
    )
}

@Composable
fun NumberPad(
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .padding(NavigatorTheme.dimensions.marginPadding),
    ) {
        NumberPadRow {
            PadKey(text = "7", onClick = onClick, modifier = Modifier.weight(1f))
            VerticalDivider()
            PadKey(text = "8", onClick = onClick, modifier = Modifier.weight(1f))
            VerticalDivider()
            PadKey(text = "9", onClick = onClick, modifier = Modifier.weight(1f))
        }
        HorizontalDivider()
        NumberPadRow {
            PadKey(text = "4", onClick = onClick, modifier = Modifier.weight(1f))
            VerticalDivider()
            PadKey(text = "5", onClick = onClick, modifier = Modifier.weight(1f))
            VerticalDivider()
            PadKey(text = "6", onClick = onClick, modifier = Modifier.weight(1f))
        }
        HorizontalDivider()
        NumberPadRow {
            PadKey(text = "1", onClick = onClick, modifier = Modifier.weight(1f))
            VerticalDivider()
            PadKey(text = "2", onClick = onClick, modifier = Modifier.weight(1f))
            VerticalDivider()
            PadKey(text = "3", onClick = onClick, modifier = Modifier.weight(1f))
        }
        HorizontalDivider()
        NumberPadRow {
            PadKey(text = "0", onClick = onClick, modifier = Modifier.weight(2f))
            VerticalDivider()
            PadKey(
                icon = R.drawable.backspace,
                onClick = { onClick(BACKSPACE) },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun NumberPadRow(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Row(
        modifier = modifier
            .height(IntrinsicSize.Min)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) { content() }
}

@Composable
private fun PadKey(
    text: String,
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    ClickableKey(
        onClick = { onClick(text) },
        modifier = modifier,
    ) {
        Text(
            text = text,
            style = NavigatorTheme.typography.displaySmall,
        )
    }
}

@Composable
private fun PadKey(
    @DrawableRes icon: Int,
    onClick: () -> Unit,
    contentDescription: String? = null,
    modifier: Modifier = Modifier,
) {
    ClickableKey(
        onClick = { onClick() },
        modifier = modifier,
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = contentDescription,
            modifier = Modifier
                .size(NavigatorTheme.dimensions.dialogKeyIconSize),
        )
    }
}

@Composable
private fun ClickableKey(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = modifier
            .size(NavigatorTheme.dimensions.minClickableSize)
            .clickable(role = Role.Button) { onClick() },
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) { content() }
}

@Composable
fun DialogButtons(
    onCancel: () -> Unit,
    onAccept: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .clickable { onCancel() }
                .weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = "Cancel",
                modifier = Modifier.size(NavigatorTheme.dimensions.dialogButtonIconSize),
            )
        }
        Column(
            modifier = Modifier
                .background(NavigatorTheme.colors.primary)
                .clickable { onAccept() }
                .weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = "Accept",
                modifier = Modifier.size(NavigatorTheme.dimensions.dialogButtonIconSize),
                tint = NavigatorTheme.colors.onPrimary,
            )
        }
    }
}

@Composable
private fun NumberField(
    backgroundMask: () -> String,
    number: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier,
    ) {
        Text(
            text = buildAnnotatedString {
                withStyle(
                    style = SpanStyle(
                        color = NavigatorTheme.colors.onPrimaryContainer.copy(alpha = 0.5f),
                    )
                ) {
                    append(backgroundMask())
                }
                append(number)
            },
            modifier = Modifier
                .fillMaxWidth()
                .background(color = NavigatorTheme.colors.primaryContainer)
                .padding(NavigatorTheme.dimensions.marginPadding),
            color = NavigatorTheme.colors.onPrimaryContainer,
            textAlign = TextAlign.End,
            style = NavigatorTheme.typography.displayLarge,
        )
    }

}
