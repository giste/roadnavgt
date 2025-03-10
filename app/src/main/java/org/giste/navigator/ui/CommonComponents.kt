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

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.giste.navigator.ui.theme.NavigatorTheme

@Composable
fun ScreenBottomBar(
    title: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .background(color = MaterialTheme.colorScheme.primary)
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .clickable { onBackClick() }
                .background(MaterialTheme.colorScheme.primaryContainer)
                .weight(1f)
                .padding(NavigatorTheme.dimensions.marginPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Default.ArrowBack,
                contentDescription = "Back",
                modifier = Modifier
                    .fillMaxSize()
                    .size(NavigatorTheme.dimensions.commandBarIconSize),
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        }

        Text(
            text = title,
            modifier = modifier
                .weight(9f)
                .padding(NavigatorTheme.dimensions.marginPadding),
            color = MaterialTheme.colorScheme.onPrimary,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineMedium,
        )
    }
}

@Composable
fun Modifier.verticalColumnScrollbar(
    scrollState: ScrollState,
    width: Dp = NavigatorTheme.dimensions.marginPadding,
    showScrollBarTrack: Boolean = true,
    scrollBarTrackColor: Color = Color.Gray,
    scrollBarColor: Color = Color.Black,
    scrollBarCornerRadius: Float = 4f,
    endPadding: Float = 12f
): Modifier {
    return drawWithContent {
        // Draw the column's content
        drawContent()
        // Dimensions and calculations
        val viewportHeight = this.size.height
        val totalContentHeight = scrollState.maxValue.toFloat() + viewportHeight

        // Only draw scrollbar if needed
        if (totalContentHeight > viewportHeight) {
            val scrollValue = scrollState.value.toFloat()
            // Compute scrollbar height and position
            val scrollBarHeight =
                (viewportHeight / totalContentHeight) * viewportHeight
            val scrollBarStartOffset =
                (scrollValue / totalContentHeight) * viewportHeight
            // Draw the track (optional)
            if (showScrollBarTrack) {
                drawRoundRect(
                    cornerRadius = CornerRadius(scrollBarCornerRadius),
                    color = scrollBarTrackColor,
                    topLeft = Offset(this.size.width - endPadding, 0f),
                    size = Size(width.toPx(), viewportHeight),
                )
            }
            // Draw the scrollbar
            drawRoundRect(
                cornerRadius = CornerRadius(scrollBarCornerRadius),
                color = scrollBarColor,
                topLeft = Offset(this.size.width - endPadding, scrollBarStartOffset),
                size = Size(width.toPx(), scrollBarHeight)
            )
        }
    }
}
