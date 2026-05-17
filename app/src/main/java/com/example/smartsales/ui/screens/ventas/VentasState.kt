package com.example.smartsales.ui.screens.ventas

import com.example.smartsales.data.local.entity.ProductoEntity

// Representa una fila en nuestro ticket de compra temporal
data class ItemCarrito(
    val producto: ProductoEntity,
    var cantidad: Int
) {
    val subtotal: Double get() = producto.precio * cantidad
}

// Representa todo lo que se ve en la pantalla
data class VentasState(
    val carrito: List<ItemCarrito> = emptyList(),
    val total: Double = 0.0,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false // Para mostrar un mensaje de "Venta cobrada"
)