package com.example.smartsales.ui.screens.productos

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.smartsales.data.local.entity.ProductoEntity
import com.example.smartsales.ui.navigation.Routes
import com.google.gson.Gson

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductosScreen(
    navController: NavController,
    viewModel: ProductosViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle
    val scannedCode = savedStateHandle?.get<String>("barcode_scanned")

    LaunchedEffect(scannedCode) {
        if (!scannedCode.isNullOrEmpty()) {
            // Mandamos el código escaneado directo al buscador
            viewModel.onSearchQueryChange(scannedCode)
            // Borramos el estado para que no se atore en el buscador al girar la pantalla
            savedStateHandle.remove<String>("barcode_scanned")
        }
    }

    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Inventario") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                actions = {
                    IconButton(onClick = { viewModel.sincronizarConServidor() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Actualizar")
                    }
                }
            )
        },
        // Botón flotante para agregar un nuevo producto
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.currentBackStackEntry?.savedStateHandle?.remove<String>("producto_json")
                    navController.navigate(Routes.GestionProducto.route)
                },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar Producto", tint = MaterialTheme.colorScheme.onPrimary)
            }
        }
    ) { paddingValues ->
        // CAMBIO PRINCIPAL: Usamos Column para apilar la barra y la lista
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            // 1. LA BARRA DE BÚSQUEDA
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = { viewModel.onSearchQueryChange(it) },
                label = { Text("Buscar por nombre o código") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                singleLine = true,
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = "Buscar")
                },
                trailingIcon = {
                    IconButton(onClick = { navController.navigate(Routes.Escaner.route) }) {
                        Icon(imageVector = Icons.Default.CameraAlt, contentDescription = "Escanear Código")
                    }
                }
            )

            // 2. EL CONTENEDOR DE LA LISTA (Usa weight(1f) para tomar el espacio restante)
            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                if (uiState.isRefreshing) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth().align(Alignment.TopCenter))
                }

                if (uiState.isLoading && uiState.productos.isEmpty()) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                } else if (uiState.productos.isEmpty()) {
                    // Mensaje dinámico si está buscando o si el inventario está vacío
                    val mensaje = if (uiState.searchQuery.isNotBlank()) {
                        "No se encontraron resultados."
                    } else {
                        "No hay productos disponibles."
                    }
                    Text(text = mensaje, modifier = Modifier.align(Alignment.Center))
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        // Añadimos margen inferior para que el botón flotante no tape el último elemento
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 80.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(uiState.productos) { producto ->
                            ProductoItem(
                                producto = producto,
                                onEditClick = {
                                    val productoJson = Gson().toJson(producto)
                                    navController.currentBackStackEntry?.savedStateHandle?.set("producto_json", productoJson)
                                    navController.navigate(Routes.GestionProducto.route)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProductoItem(
    producto: ProductoEntity,
    onEditClick: () -> Unit
) {
    // Si el producto no está activo, reducimos la opacidad general de la tarjeta a 0.5f
    val opacidadCard = if (producto.activo) 1f else 0.5f

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(opacidadCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = producto.nombre,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    // Si está inactivo, pintamos una etiqueta de aviso explícita
                    if (!producto.activo) {
                        Text(
                            text = "No Disponible (Baja Lógica)",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Botón para mandar a editar este producto específico
                IconButton(onClick = onEditClick) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Editar",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            producto.descripcion?.let {
                Text(text = it, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = "Precio: $${producto.precio}", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                Text(
                    text = "Stock: ${producto.stock}",
                    style = MaterialTheme.typography.labelLarge,
                    color = if (producto.stock < 10) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Cód: ${producto.codigo_barras}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}