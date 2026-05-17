package com.example.smartsales.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.example.smartsales.data.local.entity.DetalleVentaEntity
import com.example.smartsales.data.local.entity.VentaEntity

@Dao
interface VentaDao {

    // 1. Guarda la cabecera de la venta y nos devuelve el ID que se acaba de generar
    @Insert
    suspend fun insertarVenta(venta: VentaEntity): Long

    // 2. Guarda todos los productos de esa venta
    @Insert
    suspend fun insertarDetalles(detalles: List<DetalleVentaEntity>)

    // 3. Busca todas las ventas que NO han sido enviadas a Node.js
    @Query("SELECT * FROM ventas_table WHERE estaSincronizada = 0")
    suspend fun obtenerVentasPendientes(): List<VentaEntity>

    // 4. Obtiene los productos específicos de una venta
    @Query("SELECT * FROM detalle_ventas_table WHERE ventaLocalId = :ventaId")
    suspend fun obtenerDetallesDeVenta(ventaId: Int): List<DetalleVentaEntity>

    // 5. Marca una venta como enviada con éxito al servidor
    @Query("UPDATE ventas_table SET estaSincronizada = 1 WHERE idLocal = :ventaId")
    suspend fun marcarComoSincronizada(ventaId: Int)
}