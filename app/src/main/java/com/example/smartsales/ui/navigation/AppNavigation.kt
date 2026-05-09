package com.example.smartsales.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.smartsales.ui.screens.login.LoginScreen

@Composable
fun AppNavigation() {
    // Este controlador maneja la pila de pantallas (el historial hacia atrás)
    val navController = rememberNavController()

    // NavHost es el contenedor donde se dibujan las pantallas.
    // Le decimos que inicie en el Login.
    NavHost(navController = navController, startDestination = Routes.Login.route) {

        composable(Routes.Login.route) {
            LoginScreen(navController = navController)
        }

        composable(Routes.Productos.route) {
            PantallaTemporal("Lista de Productos")
        }

        composable(Routes.Ventas.route) {
            PantallaTemporal("Registro de Ventas")
        }

        composable(Routes.Dashboard.route) {
            PantallaTemporal("Dashboard y Gráficas")
        }
    }
}

// Función temporal solo para que veas algo en la pantalla y no de error
@Composable
fun PantallaTemporal(titulo: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = titulo)
    }
}