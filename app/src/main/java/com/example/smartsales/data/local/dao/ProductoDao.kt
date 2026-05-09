package com.example.smartsales.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.smartsales.data.local.entity.ProductoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductoDao {

    // Retorna un Flow para que la UI reaccione en tiempo real
    @Query("SELECT * FROM productos_table ORDER BY nombre ASC")
    fun obtenerProductosLocales(): Flow<List<ProductoEntity>>

    @Query("SELECT * FROM productos_table WHERE codigo_barras = :codigo LIMIT 1")
    suspend fun obtenerProductoPorCodigo(codigo: String): ProductoEntity?

    // Si insertamos productos que ya existen, los reemplaza con los datos más nuevos
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarProductos(productos: List<ProductoEntity>)

    // Borra los productos locales (útil para cuando forzamos una sincronización limpia)
    @Query("DELETE FROM productos_table")
    suspend fun limpiarProductos()
}