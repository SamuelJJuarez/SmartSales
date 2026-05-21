package com.example.smartsales.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
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

            // 1. CORRECCIÓN DE FECHA: Limpiamos el texto "2026-05-17T00:00.000Z" a "17/05"
            val fechaLimpia = venta.fecha.split("T").firstOrNull() ?: venta.fecha
            val diaMes = if (fechaLimpia.contains("-")) {
                val partes = fechaLimpia.split("-")
                if (partes.size >= 3) "${partes[2]}/${partes[1]}" else fechaLimpia
            } else {
                fechaLimpia.takeLast(2)
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxHeight()
            ) {
                Text(
                    text = "$${venta.total_dia.toInt()}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))

                // 2. CORRECCIÓN DE LA BARRA: Usamos weight(1f) para que todas las columnas midan igual
                Box(
                    modifier = Modifier
                        .width(32.dp)
                        .weight(1f),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        // Calculamos la altura de la barra internamente
                        val barHeight = size.height * heightRatio.coerceAtLeast(0.05f)

                        drawRoundRect(
                            color = barColor,
                            // Dibujamos anclando la barra a la parte inferior del Canvas
                            topLeft = Offset(x = 0f, y = size.height - barHeight),
                            size = Size(width = size.width, height = barHeight),
                            cornerRadius = CornerRadius(8f, 8f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))
                // Mostramos el texto formateado
                Text(
                    text = diaMes,
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