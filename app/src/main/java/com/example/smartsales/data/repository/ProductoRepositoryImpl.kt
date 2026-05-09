package com.example.smartsales.data.repository

import android.util.Log
import com.example.smartsales.data.local.dao.ProductoDao
import com.example.smartsales.data.local.entity.ProductoEntity
import com.example.smartsales.data.mapper.toEntity
import com.example.smartsales.data.remote.SmartSalesApi
import com.example.smartsales.domain.repository.ProductoRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ProductoRepositoryImpl @Inject constructor(
    private val api: SmartSalesApi,
    private val dao: ProductoDao
) : ProductoRepository {

    // 1. Simplemente devolvemos el Flow que ya nos da Room
    override fun obtenerProductosLocales(): Flow<List<ProductoEntity>> {
        return dao.obtenerProductosLocales()
    }

    // 2. La sincronización maestra (Modo Offline-First)
    override suspend fun sincronizarProductos() {
        try {
            // Intentamos descargar los datos de Express
            val response = api.getProductos()

            if (response.isSuccessful && response.body() != null) {
                // Convertimos los DTOs a Entidades usando el Mapper del Paso 1
                val productosNuevos = response.body()!!.map { it.toEntity() }

                // Guardamos en la base de datos local
                dao.insertarProductos(productosNuevos)
                Log.d("ProductoRepository", "Productos sincronizados con éxito")
            } else {
                Log.e("ProductoRepository", "Error de API: ${response.code()}")
            }
        } catch (e: Exception) {
            // Si hay error (ej. No hay internet), simplemente lo atrapamos.
            // La aplicación no crasheará y la UI seguirá mostrando los datos de Room.
            Log.e("ProductoRepository", "Error de red, usando caché local: ${e.message}")
        }
    }

    // 3. Buscar para el escáner (Primero intenta local, luego red)
    override suspend fun obtenerProductoPorCodigo(codigo: String): ProductoEntity? {
        // Buscamos primero en el celular
        val productoLocal = dao.obtenerProductoPorCodigo(codigo)
        if (productoLocal != null) {
            return productoLocal
        }

        // Si no está localmente, podríamos intentar buscarlo en la API como respaldo
        // (Esto es útil si hay miles de productos y no queremos descargar todos al inicio)
        return try {
            val response = api.getProductos() // Nota: Aquí idealmente usaríamos el endpoint por código que creaste
            // Para simplificar ahora, devolvemos null si no está en la base de datos
            null
        } catch (e: Exception) {
            null
        }
    }
}