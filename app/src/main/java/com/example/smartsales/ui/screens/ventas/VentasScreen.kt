package com.example.smartsales.ui.screens.ventas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.smartsales.ui.navigation.Routes
import androidx.compose.material.icons.filled.Remove
import androidx.compose.ui.text.style.TextAlign

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VentasScreen(
    navController: NavController,
    viewModel: VentasViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var codigoManual by remember { mutableStateOf("") }

    // 1. Escuchar si la pantalla del escáner nos mandó un código de regreso
    val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle
    val scannedCode = savedStateHandle?.get<String>("barcode_scanned")

    LaunchedEffect(scannedCode) {
        if (!scannedCode.isNullOrEmpty()) {
            // Si llegó un código de la cámara, lo agregamos al carrito
            viewModel.agregarProductoPorCodigo(scannedCode)
            // Borramos el código de la memoria para que no se vuelva a agregar si giramos la pantalla
            savedStateHandle.remove<String>("barcode_scanned")
        }
    }

    // 2. Manejo de Errores y Éxitos
    LaunchedEffect(uiState.error) {
        uiState.error?.let { errorMsg ->
            snackbarHostState.showSnackbar(message = errorMsg)
            viewModel.clearError()
        }
    }

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            snackbarHostState.showSnackbar("✅ Venta registrada con éxito")
            viewModel.reiniciarVenta()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Nueva Venta") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            // Botón flotante para abrir la cámara
            FloatingActionButton(
                onClick = { navController.navigate(Routes.Escaner.route) },
                containerColor = MaterialTheme.colorScheme.secondary
            ) {
                Icon(Icons.Default.CameraAlt, contentDescription = "Escanear Código")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Sección superior: Ingreso manual de código (por si no sirve la cámara)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = codigoManual,
                    onValueChange = { codigoManual = it },
                    label = { Text("Código de barras") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            if (codigoManual.isNotBlank()) {
                                viewModel.agregarProductoPorCodigo(codigoManual.trim())
                                codigoManual = ""
                            }
                        }
                    ),
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        if (codigoManual.isNotBlank()) {
                            viewModel.agregarProductoPorCodigo(codigoManual.trim())
                            codigoManual = ""
                        }
                    },
                    colors = IconButtonDefaults.iconButtonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Agregar", tint = MaterialTheme.colorScheme.onPrimary)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Divider()
            Spacer(modifier = Modifier.height(8.dp))

            // Lista central: El Carrito de compras
            if (uiState.carrito.isEmpty()) {
                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text("El carrito está vacío. Escanea un producto.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(uiState.carrito) { item ->
                        ItemCarritoView(
                            item = item,
                            // Conectamos los clics de los botones con el ViewModel
                            onAumentar = { viewModel.aumentarCantidad(item.producto.id) },
                            onDisminuir = { viewModel.disminuirCantidad(item.producto.id) }
                        )
                    }
                }
            }

            // Sección inferior: Total y Botón de Cobrar
            Card(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Total a cobrar:", style = MaterialTheme.typography.titleMedium)
                        Text(
                            text = "$${uiState.total}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { viewModel.registrarVenta() },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        enabled = uiState.carrito.isNotEmpty() && !uiState.isLoading
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                        } else {
                            Text("Cobrar Venta")
                        }
                    }
                }
            }
        }
    }
}

// Fila visual para cada producto en el carrito
@Composable
fun ItemCarritoView(item: ItemCarrito, onAumentar: () -> Unit, onDisminuir: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Columna izquierda: Nombre y precio unitario
        Column(modifier = Modifier.weight(1f)) {
            Text(item.producto.nombre, fontWeight = FontWeight.Bold)
            Text(
                text = "$${item.producto.precio} c/u",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Columna central: Los controles de cantidad (+ y -)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp)
        ) {
            // Botón de Menos / Eliminar
            IconButton(
                onClick = onDisminuir,
                modifier = Modifier.size(32.dp),
                colors = IconButtonDefaults.iconButtonColors(contentColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(Icons.Default.Remove, contentDescription = "Quitar")
            }

            // Texto de la cantidad actual
            Text(
                text = "${item.cantidad}",
                modifier = Modifier.padding(horizontal = 8.dp),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium
            )

            // Botón de Más
            IconButton(
                onClick = onAumentar,
                modifier = Modifier.size(32.dp),
                colors = IconButtonDefaults.iconButtonColors(contentColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar")
            }
        }

        // Columna derecha: El Subtotal de este producto
        Text(
            text = "$${String.format("%.2f", item.subtotal)}",
            modifier = Modifier.width(70.dp),
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.End
        )
    }
}