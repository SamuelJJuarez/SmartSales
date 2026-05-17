package com.example.smartsales.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.smartsales.data.local.dao.ProductoDao
import com.example.smartsales.data.local.entity.ProductoEntity
import com.example.smartsales.data.local.dao.VentaDao
import com.example.smartsales.data.local.entity.DetalleVentaEntity
import com.example.smartsales.data.local.entity.VentaEntity

// Aquí listamos todas las entidades que tendrá la BD local
@Database(
    entities = [ProductoEntity::class, VentaEntity::class, DetalleVentaEntity::class],
    version = 2,
    exportSchema = false
)
abstract class SmartSalesDatabase : RoomDatabase() {

    // Definimos los DAOs que Room debe generar
    abstract val productoDao: ProductoDao
    abstract val ventaDao: VentaDao

}