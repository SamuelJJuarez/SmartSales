package com.example.smartsales.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "productos_table")
data class ProductoEntity(
    @PrimaryKey val id: Int, // Usamos el mismo ID que viene de tu API en Express
    val codigo_barras: String,
    val nombre: String,
    val descripcion: String?,
    val precio: Double,
    val stock: Int,
    val activo: Boolean = true
)