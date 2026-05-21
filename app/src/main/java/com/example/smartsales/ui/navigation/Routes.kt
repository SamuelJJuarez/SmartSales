package com.example.smartsales.ui.navigation

sealed class Routes(val route: String) {
    object Login : Routes("login_screen")
    object Productos : Routes("productos_screen")
    object Escaner : Routes("escaner_screen")
    object Ventas : Routes("ventas_screen")
    object Dashboard : Routes("dashboard_screen")

    object GestionProducto : Routes("gestion_producto")
}