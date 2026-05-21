package com.example.smartsales.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.smartsales.ui.screens.dashboard.DashboardScreen
import com.example.smartsales.ui.screens.escaner.EscanerScreen
import com.example.smartsales.ui.screens.login.LoginScreen
import com.example.smartsales.ui.screens.productos.ProductosScreen
import com.example.smartsales.ui.screens.ventas.VentasScreen
import com.example.smartsales.ui.screens.productos.gestion.GestionProductoScreen
import com.example.smartsales.data.local.entity.ProductoEntity
import com.google.gson.Gson

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    // Observamos la ruta actual para saber si mostramos o no la barra inferior
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // No queremos ver el menú inferior en el Login ni en la Cámara
    val showBottomBar = currentRoute in listOf(
        Routes.Dashboard.route,
        Routes.Productos.route,
        Routes.Ventas.route
    )

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomNavigationBar(navController = navController, currentRoute = currentRoute)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Routes.Login.route,
            modifier = Modifier.padding(innerPadding) // Evita que la barra tape el contenido
        ) {
            composable(Routes.Login.route) { LoginScreen(navController = navController) }
            composable(Routes.Dashboard.route) { DashboardScreen(navController = navController) }
            composable(Routes.Productos.route) { ProductosScreen(navController = navController) }
            composable(Routes.Ventas.route) { VentasScreen(navController = navController) }
            composable(Routes.Escaner.route) { EscanerScreen(navController = navController) }
            composable(Routes.GestionProducto.route) {
                // Recuperamos el producto a editar si es que la pantalla anterior guardó uno
                val productoJson = navController.previousBackStackEntry?.savedStateHandle?.get<String>("producto_json")
                val productoAEditar = productoJson?.let { Gson().fromJson(it, ProductoEntity::class.java) }

                GestionProductoScreen(
                    navController = navController,
                    productoAEditar = productoAEditar
                )
            }
        }
    }
}

// El componente de la barra inferior
@Composable
fun BottomNavigationBar(navController: NavController, currentRoute: String?) {
    NavigationBar {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Dashboard, contentDescription = "Panel") },
            label = { Text("Panel") },
            selected = currentRoute == Routes.Dashboard.route,
            onClick = {
                navController.navigate(Routes.Dashboard.route) {
                    popUpTo(Routes.Dashboard.route) { inclusive = true }
                }
            }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Inventory, contentDescription = "Inventario") },
            label = { Text("Inventario") },
            selected = currentRoute == Routes.Productos.route,
            onClick = {
                navController.navigate(Routes.Productos.route) {
                    popUpTo(Routes.Dashboard.route)
                }
            }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.ShoppingCart, contentDescription = "Vender") },
            label = { Text("Vender") },
            selected = currentRoute == Routes.Ventas.route,
            onClick = {
                navController.navigate(Routes.Ventas.route) {
                    popUpTo(Routes.Dashboard.route)
                }
            }
        )
    }
}