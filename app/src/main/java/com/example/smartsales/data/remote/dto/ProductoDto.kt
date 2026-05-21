package com.example.smartsales.data.remote.dto

data class ProductoDto(
    val id: Int? = null,
    val codigo_barras: String,
    val nombre: String,
    val descripcion: String?,
    val precio: Double,
    val stock: Int,
    val activo: Boolean = true
)