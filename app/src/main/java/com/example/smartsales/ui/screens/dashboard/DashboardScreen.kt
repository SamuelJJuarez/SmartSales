package com.example.smartsales.ui.screens.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Inventory
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    navController: NavController,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

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
        if (uiState.isLoading) {
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
                Text(
                    text = "Resumen del Sistema",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                // Tarjeta 1: Valor del Inventario
                KpiCard(
                    titulo = "Valor del Inventario",
                    valor = "$${String.format("%.2f", uiState.valorTotalInventario)}",
                    icono = Icons.Default.AttachMoney,
                    colorFondo = MaterialTheme.colorScheme.primaryContainer,
                    colorTexto = MaterialTheme.colorScheme.onPrimaryContainer
                )

                // Tarjeta 2: Total de Productos
                KpiCard(
                    titulo = "Productos en Catálogo",
                    valor = "${uiState.totalProductosRegistrados} ítems",
                    icono = Icons.Default.Inventory,
                    colorFondo = MaterialTheme.colorScheme.secondaryContainer,
                    colorTexto = MaterialTheme.colorScheme.onSecondaryContainer
                )

                // Tarjeta 3: Alertas de Stock
                KpiCard(
                    titulo = "Alertas de Bajo Stock",
                    valor = "${uiState.productosBajoStock} productos",
                    icono = Icons.Default.Warning,
                    colorFondo = if (uiState.productosBajoStock > 0) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.tertiaryContainer,
                    colorTexto = if (uiState.productosBajoStock > 0) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onTertiaryContainer
                )
            }
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