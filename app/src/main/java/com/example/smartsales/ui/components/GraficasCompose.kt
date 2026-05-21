package com.example.smartsales.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.smartsales.data.remote.dto.VentaDiaDto

@Composable
fun VentasBarChart(ventas: List<VentaDiaDto>, modifier: Modifier = Modifier) {
    if (ventas.isEmpty()) return

    val maxVenta = ventas.maxOf { it.total_dia }.toFloat()
    val barColor = MaterialTheme.colorScheme.primary

    Row(
        modifier = modifier.fillMaxWidth().height(200.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Bottom
    ) {
        ventas.forEach { venta ->
            val heightRatio = if (maxVenta > 0) (venta.total_dia.toFloat() / maxVenta) else 0f

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom,
                modifier = Modifier.fillMaxHeight()
            ) {
                Text(
                    text = "$${venta.total_dia.toInt()}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Canvas(modifier = Modifier.width(32.dp).fillMaxHeight(heightRatio.coerceAtLeast(0.05f))) {
                    drawRoundRect(
                        color = barColor,
                        size = Size(size.width, size.height),
                        cornerRadius = CornerRadius(8f, 8f)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                // Mostrar solo el día (ej. "2026-05-17" -> "17")
                Text(
                    text = venta.fecha.takeLast(2),
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}

@Composable
fun VentasLineChart(ventas: List<VentaDiaDto>, modifier: Modifier = Modifier) {
    if (ventas.isEmpty()) return

    val maxVenta = ventas.maxOf { it.total_dia }.toFloat()
    val lineColor = MaterialTheme.colorScheme.secondary

    Canvas(modifier = modifier.fillMaxWidth().height(150.dp).padding(16.dp)) {
        val width = size.width
        val height = size.height
        val stepX = width / (ventas.size - 1).coerceAtLeast(1)

        val path = Path()

        ventas.forEachIndexed { index, venta ->
            val x = index * stepX
            val y = height - (if (maxVenta > 0) (venta.total_dia.toFloat() / maxVenta) * height else 0f)

            if (index == 0) {
                path.moveTo(x, y)
            } else {
                path.lineTo(x, y)
            }
        }

        drawPath(
            path = path,
            color = lineColor,
            style = Stroke(
                width = 6f,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )
    }
}