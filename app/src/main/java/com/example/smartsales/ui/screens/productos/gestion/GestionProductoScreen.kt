package com.example.smartsales.ui.screens.productos.gestion

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.smartsales.data.local.entity.ProductoEntity
import com.example.smartsales.ui.navigation.Routes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GestionProductoScreen(
    navController: NavController,
    productoAEditar: ProductoEntity? = null,
    viewModel: GestionProductoViewModel = hiltViewModel()
) {
    LaunchedEffect(productoAEditar) {
        if (productoAEditar != null) {
            viewModel.cargarProducto(productoAEditar)
        } else {
            // Si es un producto nuevo, barremos el formulario
            viewModel.limpiarCampos()
        }
    }

    val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle
    val scannedCode = savedStateHandle?.get<String>("barcode_scanned")

    LaunchedEffect(scannedCode) {
        scannedCode?.let {
            viewModel.codigoBarras = it
            savedStateHandle.remove<String>("barcode_scanned")
        }
    }

    // ¡NUEVO! Observador para cerrar la pantalla automáticamente al tener éxito
    LaunchedEffect(viewModel.isSuccess) {
        if (viewModel.isSuccess) {
            // Cerramos la pantalla actual y regresamos al catálogo
            navController.popBackStack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (viewModel.id == null) "Nuevo Producto" else "Editar Producto") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Manejo de mensajes de error
            viewModel.error?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
            }

            OutlinedTextField(
                value = viewModel.codigoBarras,
                onValueChange = { if(viewModel.id == null) viewModel.codigoBarras = it },
                label = { Text("Código de Barras") },
                modifier = Modifier.fillMaxWidth(),
                enabled = viewModel.id == null,
                trailingIcon = {
                    if(viewModel.id == null) {
                        IconButton(onClick = { navController.navigate(Routes.Escaner.route) }) {
                            Icon(Icons.Default.CameraAlt, contentDescription = "Escanear")
                        }
                    }
                }
            )

            OutlinedTextField(
                value = viewModel.nombre,
                onValueChange = { viewModel.nombre = it },
                label = { Text("Nombre del Producto") },
                modifier = Modifier.fillMaxWidth()
            )

            // ¡NUEVO! Campo de Descripción
            OutlinedTextField(
                value = viewModel.descripcion,
                onValueChange = { viewModel.descripcion = it },
                label = { Text("Descripción (Opcional)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2
            )

            OutlinedTextField(
                value = viewModel.precio,
                onValueChange = { viewModel.precio = it },
                label = { Text("Precio de Venta") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = viewModel.stock,
                onValueChange = { viewModel.stock = it },
                label = { Text("Stock Actual") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Card(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Estado del Producto", fontWeight = FontWeight.Bold)
                        Text(
                            text = if (viewModel.activo) "Disponible para venta" else "Inactivo (Baja lógica)",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(checked = viewModel.activo, onCheckedChange = { viewModel.activo = it })
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { viewModel.guardar() },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                enabled = !viewModel.isLoading
            ) {
                if (viewModel.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("Guardar Producto")
                }
            }
        }
    }
}