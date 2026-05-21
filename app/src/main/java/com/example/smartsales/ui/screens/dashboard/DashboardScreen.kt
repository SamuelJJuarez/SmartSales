package com.example.smartsales.ui.screens.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.smartsales.ui.components.VentasBarChart
import com.example.smartsales.ui.components.VentasLineChart
import java.util.Locale
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import android.Manifest
import android.os.Build
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.isGranted

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun DashboardScreen(
    navController: NavController,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val notificationPermissionState = rememberPermissionState(Manifest.permission.POST_NOTIFICATIONS)

        LaunchedEffect(Unit) {
            if (!notificationPermissionState.status.isGranted) {
                notificationPermissionState.launchPermissionRequest()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Panel de Control") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        if (uiState.isLoading && uiState.ingresosTotalesNube == 0.0) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                uiState.error?.let {
                    Text(text = "Modo Offline: $it", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelMedium)
                }

                // Métrica de la Nube (Ingresos Reales Históricos)
                KpiCard(
                    titulo = "Ingresos Históricos Totales",
                    valor = "$${String.format(Locale.getDefault(), "%.2f", uiState.ingresosTotalesNube)}",
                    icono = Icons.Default.AttachMoney,
                    colorFondo = MaterialTheme.colorScheme.primaryContainer,
                    colorTexto = MaterialTheme.colorScheme.onPrimaryContainer
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    // Métrica Local (Alertas de Stock)
                    Box(modifier = Modifier.weight(1f)) {
                        KpiCard(
                            titulo = "Alertas Stock",
                            valor = "${uiState.productosBajoStock}",
                            icono = Icons.Default.Warning,
                            colorFondo = if (uiState.productosBajoStock > 0) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.tertiaryContainer,
                            colorTexto = if (uiState.productosBajoStock > 0) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    }
                    // Métrica Local (Catálogo Activo)
                    Box(modifier = Modifier.weight(1f)) {
                        KpiCard(
                            titulo = "Catálogo",
                            valor = "${uiState.totalProductosLocales}",
                            icono = Icons.Default.Inventory,
                            colorFondo = MaterialTheme.colorScheme.secondaryContainer,
                            colorTexto = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider()

                Text(
                    text = "Tendencia de Ventas (Últimos 7 días)",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                )

                if (uiState.ventasPorDia.isNotEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Gráfica de Barras", style = MaterialTheme.typography.labelMedium)
                            VentasBarChart(ventas = uiState.ventasPorDia)

                            Spacer(modifier = Modifier.height(24.dp))

                            Text("Gráfica de Líneas", style = MaterialTheme.typography.labelMedium)
                            VentasLineChart(ventas = uiState.ventasPorDia)
                        }
                    }
                } else {
                    Text("No hay datos recientes para graficar.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                // Sección de la Nube: Top 5 Productos
                Text(
                    text = "Top 5 Más Vendidos",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                if (uiState.topProductos.isEmpty()) {
                    Text("No hay suficientes datos de ventas aún.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                } else {
                    uiState.topProductos.forEach { producto ->
                        TopProductoItem(producto.nombre, producto.total_vendido)
                    }
                }

                Spacer(modifier = Modifier.height(64.dp)) // Espacio para que el menú inferior no tape
            }
        }
    }
}

@Composable
fun TopProductoItem(nombre: String, cantidad: Int) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Star, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = nombre, fontWeight = FontWeight.Bold)
            }
            Text(text = "$cantidad uds", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
        }
    }
}

// Componente reutilizable para dibujar las tarjetas
@Composable
fun KpiCard(
    titulo: String,
    valor: String,
    icono: ImageVector,
    colorFondo: androidx.compose.ui.graphics.Color,
    colorTexto: androidx.compose.ui.graphics.Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = colorFondo),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = titulo, style = MaterialTheme.typography.labelLarge, color = colorTexto.copy(alpha = 0.8f))
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = valor, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = colorTexto)
            }
            Icon(
                imageVector = icono,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = colorTexto.copy(alpha = 0.5f)
            )
        }
    }
}