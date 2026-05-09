package com.example.smartsales.data.remote.dto

data class ProductoDto(
    val id: Int,
    val codigo_barras: String,
    val nombre: String,
    val descripcion: String?,
    val precio: Double,
    val stock: Int
)