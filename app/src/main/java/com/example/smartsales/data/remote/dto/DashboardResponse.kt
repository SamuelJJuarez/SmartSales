package com.example.smartsales.data.remote.dto

data class DashboardResponse(
    val ingresos_totales: Double,
    val ventas_por_dia: List<VentaDiaDto>,
    val productos_top: List<ProductoTopDto>
)

data class VentaDiaDto(
    val fecha: String,
    val total_dia: Double
)

data class ProductoTopDto(
    val nombre: String,
    val total_vendido: Int
)