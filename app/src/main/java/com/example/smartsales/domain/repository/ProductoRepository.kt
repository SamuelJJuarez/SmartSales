package com.example.smartsales.domain.repository

import com.example.smartsales.data.local.entity.ProductoEntity
import kotlinx.coroutines.flow.Flow

interface ProductoRepository {

    // Este Flow siempre emitirá la lista de productos almacenados en el celular
    fun obtenerProductosLocales(): Flow<List<ProductoEntity>>

    // Esta función forzará la descarga de datos de la API para guardarlos localmente
    suspend fun sincronizarProductos()

    // Buscar un producto específico por su código de barras
    suspend fun obtenerProductoPorCodigo(codigo: String): ProductoEntity?
}