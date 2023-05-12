package ru.blays.revanced.Presentation.Elements.GradientProgressIndicator

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.progressSemantics
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import kotlin.math.abs

internal val LinearIndicatorWidth = 240.dp
internal val LinearIndicatorHeight = 4.0.dp

private fun DrawScope.drawLinearIndicatorTrack(
    brush: Brush,
    strokeWidth: Float,
    strokeCap: StrokeCap,
) = drawLinearIndicator(0f, 1f, brush, strokeWidth, strokeCap)

private fun DrawScope.drawLinearIndicator(
    startFraction: Float,
    endFraction: Float,
    brush: Brush,
    strokeWidth: Float,
    strokeCap: StrokeCap,
) {
    val width = size.width
    val height = size.height
    // Start drawing from the vertical center of the stroke
    val yOffset = height / 2

    val isLtr = layoutDirection == LayoutDirection.Ltr
    val barStart = (if (isLtr) startFraction else 1f - endFraction) * width
    val barEnd = (if (isLtr) endFraction else 1f - startFraction) * width

    // if there isn't enough space to draw the stroke caps, fall back to StrokeCap.Butt
    if (strokeCap == StrokeCap.Butt || height > width) {
        // Progress line
        drawLine(brush, Offset(barStart, yOffset), Offset(barEnd, yOffset), strokeWidth)
    } else {
        // need to adjust barStart and barEnd for the stroke caps
        val strokeCapOffset = strokeWidth / 2
        val coerceRange = strokeCapOffset..(width - strokeCapOffset)
        val adjustedBarStart = barStart.coerceIn(coerceRange)
        val adjustedBarEnd = barEnd.coerceIn(coerceRange)

        if (abs(endFraction - startFraction) > 0) {
            // Progress line
            drawLine(
                brush,
                Offset(adjustedBarStart, yOffset),
                Offset(adjustedBarEnd, yOffset),
                strokeWidth,
                strokeCap,
            )
        }
    }
}

@Composable
fun GradientLinearProgressIndicator(
    progress: Float,
    modifier: Modifier = Modifier,
    brush: Brush,
    trackColor: Brush = SolidColor(MaterialTheme.colorScheme.surfaceVariant),
    strokeCap: StrokeCap = ProgressIndicatorDefaults.LinearStrokeCap,
) {
    val coercedProgress = progress.coerceIn(0f, 1f)
    Canvas(
        modifier
            .progressSemantics(coercedProgress)
            .size(LinearIndicatorWidth, LinearIndicatorHeight)
    ) {
        val strokeWidth = size.height
        drawLinearIndicatorTrack(trackColor, strokeWidth, strokeCap)
        drawLinearIndicator(0f, coercedProgress, brush, strokeWidth, strokeCap)
    }
}