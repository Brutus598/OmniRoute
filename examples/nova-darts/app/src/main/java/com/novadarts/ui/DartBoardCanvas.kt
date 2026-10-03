package com.novadarts.ui

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.novadarts.game.AimPoint
import com.novadarts.game.DartScoring
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

@Composable
fun DartBoardCanvas(
    modifier: Modifier = Modifier,
    aim: AimPoint? = null,
    highlightedTarget: Int? = null,
    accent: Color = NovaPalette.lime,
    onAim: (AimPoint) -> Unit,
) {
    val currentOnAim = rememberUpdatedState(onAim)
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .semantics { contentDescription = "Dartboard. Tippe auf ein Feld, um den Wurf zu zielen." }
            .pointerInput(Unit) {
                detectTapGestures { touch ->
                    val side = min(size.width, size.height)
                    val center = Offset(size.width / 2f, size.height / 2f)
                    val boardRadius = side * 0.39f
                    currentOnAim.value(
                        AimPoint(
                            x = (touch.x - center.x) / boardRadius,
                            y = (touch.y - center.y) / boardRadius,
                        ),
                    )
                }
            },
    ) {
        val side = min(size.width, size.height)
        val center = Offset(size.width / 2f, size.height / 2f)
        val radius = side * 0.39f

        // Layered dark rim makes the vector board feel like a machined, back-lit instrument.
        drawCircle(Color(0xFF27313A), radius = radius + side * 0.025f, center = center)
        drawCircle(Color(0xFF090C10), radius = radius + side * 0.016f, center = center)
        drawCircle(Color(0xFF58636C), radius = radius + side * 0.009f, center = center, style = Stroke(side * 0.003f))
        drawCircle(Color(0xFF131920), radius = radius, center = center)

        for (index in DartScoring.sectors.indices) {
            val start = -99f + index * 18f
            val dark = index % 2 == 0
            val singleColor = if (dark) NovaPalette.boardBlack else NovaPalette.boardCream
            val ringColor = if (dark) NovaPalette.boardRed else NovaPalette.boardGreen

            drawPath(ringSector(center, radius * 0.095f, radius * 0.50f, start, 18f), singleColor)
            drawPath(ringSector(center, radius * 0.58f, radius * 0.92f, start, 18f), singleColor)
            drawPath(ringSector(center, radius * 0.50f, radius * 0.58f, start, 18f), ringColor)
            drawPath(ringSector(center, radius * 0.92f, radius, start, 18f), ringColor)
        }

        drawCircle(NovaPalette.boardGreen, radius = radius * 0.095f, center = center)
        drawCircle(NovaPalette.boardRed, radius = radius * 0.038f, center = center)

        if (highlightedTarget != null) {
            if (highlightedTarget == 25) {
                drawCircle(accent.copy(alpha = 0.28f), radius = radius * 0.095f, center = center)
                drawCircle(accent, radius = radius * 0.095f, center = center, style = Stroke(side * 0.009f))
            } else {
                val sectorIndex = DartScoring.sectors.indexOf(highlightedTarget)
                if (sectorIndex >= 0) {
                    drawPath(
                        ringSector(center, radius * 0.095f, radius, -99f + sectorIndex * 18f, 18f),
                        accent.copy(alpha = 0.13f),
                    )
                    drawPath(
                        ringSector(center, radius * 0.50f, radius * 0.58f, -99f + sectorIndex * 18f, 18f),
                        accent.copy(alpha = 0.42f),
                    )
                }
            }
        }

        val wire = Color(0xFFB7C0C4).copy(alpha = 0.74f)
        listOf(0.038f, 0.095f, 0.50f, 0.58f, 0.92f, 1f).forEach { ring ->
            drawCircle(wire, radius = radius * ring, center = center, style = Stroke(side * 0.0035f))
        }
        for (index in DartScoring.sectors.indices) {
            val angle = Math.toRadians((-99.0 + index * 18.0))
            drawLine(
                color = wire,
                start = Offset(center.x + cos(angle).toFloat() * radius * 0.095f, center.y + sin(angle).toFloat() * radius * 0.095f),
                end = Offset(center.x + cos(angle).toFloat() * radius, center.y + sin(angle).toFloat() * radius),
                strokeWidth = side * 0.0032f,
            )
        }

        // Number ring: upright, high-contrast labels stay legible when the board scales down.
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = android.graphics.Color.rgb(225, 232, 235)
            textAlign = Paint.Align.CENTER
            textSize = side * 0.038f
            typeface = Typeface.create("sans-serif-condensed", Typeface.BOLD)
        }
        val baselineOffset = -(paint.ascent() + paint.descent()) / 2f
        val labelRadius = radius + side * 0.045f
        DartScoring.sectors.forEachIndexed { index, value ->
            val angle = Math.toRadians(-90.0 + index * 18.0)
            val x = center.x + cos(angle).toFloat() * labelRadius
            val y = center.y + sin(angle).toFloat() * labelRadius
            drawContext.canvas.nativeCanvas.drawText(value.toString(), x, y + baselineOffset, paint)
        }

        aim?.let { point ->
            val marker = Offset(center.x + point.x * radius, center.y + point.y * radius)
            drawCircle(accent.copy(alpha = 0.2f), radius = side * 0.044f, center = marker)
            drawCircle(accent, radius = side * 0.012f, center = marker)
            drawCircle(Color.White, radius = side * 0.004f, center = marker)
            drawCircle(Color.White.copy(alpha = 0.85f), radius = side * 0.027f, center = marker, style = Stroke(side * 0.003f))
        }
    }
}

@Composable
fun BoardIllustration(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.aspectRatio(1f)) {
        val radius = min(size.width, size.height) * 0.47f
        val center = Offset(size.width / 2f, size.height / 2f)
        drawCircle(Color(0xFF28323A), radius = radius, center = center)
        drawCircle(Color(0xFF10161C), radius = radius * 0.94f, center = center)
        drawCircle(NovaPalette.boardBlack, radius = radius * 0.90f, center = center)
        drawCircle(NovaPalette.boardCream, radius = radius * 0.69f, center = center)
        drawCircle(NovaPalette.boardBlack, radius = radius * 0.54f, center = center)
        drawCircle(NovaPalette.boardRed, radius = radius * 0.31f, center = center)
        drawCircle(NovaPalette.boardGreen, radius = radius * 0.17f, center = center)
        drawCircle(NovaPalette.lime, radius = radius * 0.09f, center = center)
        drawCircle(Color.White.copy(alpha = 0.28f), radius = radius, center = center, style = Stroke(size.minDimension * 0.012f))
    }
}

private fun ringSector(
    center: Offset,
    innerRadius: Float,
    outerRadius: Float,
    startDegrees: Float,
    sweepDegrees: Float,
): Path {
    val outer = Rect(
        center.x - outerRadius,
        center.y - outerRadius,
        center.x + outerRadius,
        center.y + outerRadius,
    )
    val inner = Rect(
        center.x - innerRadius,
        center.y - innerRadius,
        center.x + innerRadius,
        center.y + innerRadius,
    )
    val start = Math.toRadians(startDegrees.toDouble())
    val end = Math.toRadians((startDegrees + sweepDegrees).toDouble())
    return Path().apply {
        moveTo(center.x + cos(start).toFloat() * outerRadius, center.y + sin(start).toFloat() * outerRadius)
        arcTo(outer, startDegrees, sweepDegrees, false)
        lineTo(center.x + cos(end).toFloat() * innerRadius, center.y + sin(end).toFloat() * innerRadius)
        arcTo(inner, startDegrees + sweepDegrees, -sweepDegrees, false)
        close()
    }
}
