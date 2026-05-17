package com.example.smartsales.data.remote.dto

data class VentaRequest(
    val productos: List<DetalleVentaDto>
)

data class DetalleVentaDto(
    val producto_id: Int,
    val cantidad: Int
)