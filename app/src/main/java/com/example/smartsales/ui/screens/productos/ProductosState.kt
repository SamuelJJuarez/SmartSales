package com.example.smartsales.ui.screens.productos

import com.example.smartsales.data.local.entity.ProductoEntity

data class ProductosState(
    val isLoading: Boolean = false,
    val productos: List<ProductoEntity> = emptyList(), // La lista que pintaremos en Compose
    val error: String? = null,
    val isRefreshing: Boolean = false // Para cuando el usuario jale la pantalla hacia abajo para actualizar
)