package com.example.smartsales.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "detalle_ventas_table")
data class DetalleVentaEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val ventaLocalId: Int, // Para saber a qué venta pertenece
    val producto_id: Int,
    val cantidad: Int,
    val precio_unitario: Double,
    val subtotal: Double
)