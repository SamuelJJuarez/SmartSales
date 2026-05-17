package com.example.smartsales.domain.repository

import com.example.smartsales.data.local.entity.DetalleVentaEntity

interface VentaRepository {
    // Registra una nueva venta generada en la app
    suspend fun registrarVenta(detalles: List<DetalleVentaEntity>, total: Double): Result<Unit>

    // Función para buscar las ventas que se hicieron sin internet y subirlas
    suspend fun sincronizarVentasPendientes()
}