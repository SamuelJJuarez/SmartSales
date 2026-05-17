package com.example.smartsales.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ventas_table")
data class VentaEntity(
    @PrimaryKey(autoGenerate = true)
    val idLocal: Int = 0, // ID generado por el celular
    val total: Double,
    val fecha: Long = System.currentTimeMillis(),
    val estaSincronizada: Boolean = false // ¡CLAVE! Si es false, significa que debemos enviarla a Node.js cuando regrese el internet
)