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
import android.icu.text.NumberFormat
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import org.giste.navigator.R
import org.giste.navigator.ui.SetNumberDialogTags.ACCEPT_BUTTON
import org.giste.navigator.ui.SetNumberDialogTags.CANCEL_BUTTON
import org.giste.navigator.ui.SetNumberDialogTags.DESCRIPTION
import org.giste.navigator.ui.SetNumberDialogTags.KEY_0
import org.giste.navigator.ui.SetNumberDialogTags.KEY_1
import org.giste.navigator.ui.SetNumberDialogTags.KEY_2
import org.giste.navigator.ui.SetNumberDialogTags.KEY_3
import org.giste.navigator.ui.SetNumberDialogTags.KEY_4
import org.giste.navigator.ui.SetNumberDialogTags.KEY_5
import org.giste.navigator.ui.SetNumberDialogTags.KEY_6
import org.giste.navigator.ui.SetNumberDialogTags.KEY_7
import org.giste.navigator.ui.SetNumberDialogTags.KEY_8
import org.giste.navigator.ui.SetNumberDialogTags.KEY_9
import org.giste.navigator.ui.SetNumberDialogTags.KEY_DELETE
import org.giste.navigator.ui.SetNumberDialogTags.NUMBER_FIELD
import org.giste.navigator.ui.SetNumberDialogTags.TITLE
import org.giste.navigator.ui.theme.NavigatorTheme

private const val DELETE = '<'

object SetNumberDialogTags {
    const val TITLE = "TITLE"
    const val DESCRIPTION = "DESCRIPTION"
    const val NUMBER_FIELD = "NUMBER_FIELD"
    const val ACCEPT_BUTTON = "ACCEPT_BUTTON"
    const val CANCEL_BUTTON = "CANCEL_BUTTON"
    const val KEY_0 = "KEY_0"
    const val KEY_1 = "KEY_1"
    const val KEY_2 = "KEY_2"
    const val KEY_3 = "KEY_3"
    const val KEY_4 = "KEY_4"
    const val KEY_5 = "KEY_5"
    const val KEY_6 = "KEY_6"
    const val KEY_7 = "KEY_7"
    const val KEY_8 = "KEY_8"
    const val KEY_9 = "KEY_9"
    const val KEY_DELETE = "KEY_DELETE"
}

@Preview(
    name = "Tab Active 3 Landscape",
    showBackground = true,
    device = "spec:width=1920px,height=1200px,dpi=360, isRound=false, orientation=landscape",
)
@Preview(
    name = "Tab Active 3 Portrait",
    showBackground = true,
    device = "spec:width=1920px,height=1200px,dpi=360, isRound=false, orientation=portrait",
)
@Composable
private fun SetNumberDialogPreview() {
    NavigatorTheme(dynamicColor = false, darkTheme = false) {
        SetNumberDialog(
            showDialog = remember { mutableStateOf(true) },
            title = "Title",
            description = "Description of the requested number",
            number = 1234,
            numberOfIntegerDigits = 4,
            numberOfDecimalDigits = 2,
            onAccept = { },
        )
    }
}

@Composable
fun SetNumberDialog(
    showDialog: MutableState<Boolean>,
    title: String,
    description: String,
    number: Int,
    numberOfIntegerDigits: Int,
    numberOfDecimalDigits: Int,
    onAccept: (Int) -> Unit,
    decimalFormatSymbols: DecimalFormatSymbols = DecimalFormatSymbols.getInstance(),
) {
    val digits by rememberSaveable { mutableStateOf(decimalFormatSymbols.digits) }
    val numberFormat by rememberSaveable {
        val nf = NumberFormat.getInstance(decimalFormatSymbols.locale)
        with(nf) {
            isGroupingUsed = true
            minimumFractionDigits = numberOfDecimalDigits
            maximumFractionDigits = numberOfDecimalDigits
            minimumIntegerDigits = 1
            maximumIntegerDigits = numberOfIntegerDigits
        }
        mutableStateOf(nf)
    }
    val decimalFactor by rememberSaveable {
        var scale = 1
        for (i in 1..numberOfDecimalDigits) {
            scale *= 10
        }

        mutableIntStateOf(scale)
    }
    val maxNumber by rememberSaveable {
        var max = 1
        for (i in 1..numberOfIntegerDigits) {
            max *= 10
        }

        mutableIntStateOf(max.minus(1))
    }

    var currentNumber by rememberSaveable {
        mutableIntStateOf(number.coerceAtLeast(0).coerceAtMost(maxNumber))
    }

    fun processKey(key: Char) {
        currentNumber = when (key) {
            DELETE -> currentNumber.div(10)

            else -> if (currentNumber.toString().length < (numberOfIntegerDigits + numberOfDecimalDigits)) {
                currentNumber.times(10).plus(digits.indexOf(key))
            } else {
                currentNumber
            }
        }
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
                                .testTag(DESCRIPTION)
                                .fillMaxWidth()
                                .testTag("")
                                .padding(NavigatorTheme.dimensions.marginPadding),
                            overflow = TextOverflow.Clip,
                            softWrap = true,
                            style = NavigatorTheme.typography.bodyLarge,
                        )
                        NumberField(
                            number = numberFormat.format(currentNumber.div(decimalFactor.toFloat())),
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
                            digits = digits,
                            onClick = { key -> processKey(key) }
                        )
                    }
                }
                HorizontalDivider()
                DialogButtons(
                    onCancel = { showDialog.value = false },
                    onAccept = {
                        onAccept(currentNumber)
                        showDialog.value = false
                    },
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
            .testTag(TITLE)
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary)
            .padding(NavigatorTheme.dimensions.marginPadding),
        color = MaterialTheme.colorScheme.onPrimary,
        style = MaterialTheme.typography.titleLarge,
    )
}

@Composable
private fun NumberField(
    number: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier,
    ) {
        Text(
            text = number,
            modifier = Modifier
                .testTag(NUMBER_FIELD)
                .fillMaxWidth()
                .background(color = MaterialTheme.colorScheme.primaryContainer)
                .padding(NavigatorTheme.dimensions.marginPadding),
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            textAlign = TextAlign.End,
            style = MaterialTheme.typography.displayLarge,
        )
    }
}

@Composable
fun NumberPad(
    digits: CharArray,
    onClick: (Char) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .padding(NavigatorTheme.dimensions.marginPadding),
    ) {
        NumberPadRow {
            PadKey(
                key = digits[7],
                onClick = onClick,
                modifier = Modifier
                    .weight(1f)
                    .testTag(KEY_7)
            )
            VerticalDivider()
            PadKey(
                key = digits[8],
                onClick = onClick,
                modifier = Modifier
                    .weight(1f)
                    .testTag(KEY_8)
            )
            VerticalDivider()
            PadKey(
                key = digits[9],
                onClick = onClick,
                modifier = Modifier
                    .weight(1f)
                    .testTag(KEY_9)
            )
        }
        HorizontalDivider()
        NumberPadRow {
            PadKey(
                key = digits[4],
                onClick = onClick,
                modifier = Modifier
                    .weight(1f)
                    .testTag(KEY_4)
            )
            VerticalDivider()
            PadKey(
                key = digits[5],
                onClick = onClick,
                modifier = Modifier
                    .weight(1f)
                    .testTag(KEY_5)
            )
            VerticalDivider()
            PadKey(
                key = digits[6],
                onClick = onClick,
                modifier = Modifier
                    .weight(1f)
                    .testTag(KEY_6)
            )
        }
        HorizontalDivider()
        NumberPadRow {
            PadKey(
                key = digits[1],
                onClick = onClick,
                modifier = Modifier
                    .weight(1f)
                    .testTag(KEY_1)
            )
            VerticalDivider()
            PadKey(
                key = digits[2],
                onClick = onClick,
                modifier = Modifier
                    .weight(1f)
                    .testTag(KEY_2)
            )
            VerticalDivider()
            PadKey(
                key = digits[3],
                onClick = onClick,
                modifier = Modifier
                    .weight(1f)
                    .testTag(KEY_3)
            )
        }
        HorizontalDivider()
        NumberPadRow {
            PadKey(
                key = digits[0],
                onClick = onClick,
                modifier = Modifier
                    .weight(2f)
                    .testTag(KEY_0)
            )
            VerticalDivider()
            PadKey(
                icon = R.drawable.backspace,
                onClick = { onClick(DELETE) },
                modifier = Modifier
                    .weight(1f)
                    .testTag(KEY_DELETE),
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
    key: Char,
    onClick: (Char) -> Unit,
    modifier: Modifier = Modifier,
) {
    ClickableKey(
        onClick = { onClick(key) },
        modifier = modifier,
    ) {
        Text(
            text = key.toString(),
            style = MaterialTheme.typography.displaySmall,
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
                .testTag(CANCEL_BUTTON)
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
                .testTag(ACCEPT_BUTTON)
                .background(MaterialTheme.colorScheme.primary)
                .clickable { onAccept() }
                .weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = "Accept",
                modifier = Modifier.size(NavigatorTheme.dimensions.dialogButtonIconSize),
                tint = MaterialTheme.colorScheme.onPrimary,
            )
        }
    }
}
