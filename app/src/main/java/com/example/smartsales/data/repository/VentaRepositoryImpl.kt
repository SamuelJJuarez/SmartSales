package com.example.smartsales.data.repository

import android.util.Log
import com.example.smartsales.data.local.dao.VentaDao
import com.example.smartsales.data.local.entity.DetalleVentaEntity
import com.example.smartsales.data.local.entity.VentaEntity
import com.example.smartsales.data.remote.SmartSalesApi
import com.example.smartsales.data.remote.dto.DetalleVentaDto
import com.example.smartsales.data.remote.dto.VentaRequest
import com.example.smartsales.domain.repository.VentaRepository
import com.example.smartsales.util.NetworkUtils
import com.example.smartsales.util.NotificationHelper
import javax.inject.Inject

class VentaRepositoryImpl @Inject constructor(
    private val ventaDao: VentaDao,
    private val api: SmartSalesApi,
    private val networkUtils: NetworkUtils,
    private val notificationHelper: NotificationHelper
) : VentaRepository {

    override suspend fun registrarVenta(detalles: List<DetalleVentaEntity>, total: Double): Result<Unit> {
        return try {
            // 1. Guardamos la venta cabecera localmente (como NO sincronizada por defecto)
            val nuevaVenta = VentaEntity(total = total, estaSincronizada = false)
            val idLocalVenta = ventaDao.insertarVenta(nuevaVenta).toInt()

            // 2. Le asignamos ese ID generado a todos los productos y los guardamos
            val detallesConId = detalles.map { it.copy(ventaLocalId = idLocalVenta) }
            ventaDao.insertarDetalles(detallesConId)

            // 3. Intentamos subirla a Node.js INMEDIATAMENTE
            if (networkUtils.isNetworkAvailable()) {
                subirVentaAlServidor(idLocalVenta, detallesConId)
            }

            // 4. Retornamos éxito SIEMPRE, porque la venta ya está segura en SQLite
            Result.success(Unit)

        } catch (e: Exception) {
            Log.e("VentaRepository", "Error fatal al guardar venta local: ${e.message}")
            Result.failure(Exception("Error al registrar la venta en el dispositivo"))
        }
    }

    override suspend fun sincronizarVentasPendientes() {
        if (!networkUtils.isNetworkAvailable()) return

        try {
            // Buscamos las ventas que se hicieron offline
            val ventasPendientes = ventaDao.obtenerVentasPendientes()

            for (venta in ventasPendientes) {
                val detalles = ventaDao.obtenerDetallesDeVenta(venta.idLocal)
                subirVentaAlServidor(venta.idLocal, detalles)
            }
        } catch (e: Exception) {
            Log.e("VentaRepository", "Error sincronizando pendientes: ${e.message}")
        }
    }

    // Función auxiliar privada para mapear y enviar la petición
    private suspend fun subirVentaAlServidor(idLocal: Int, detalles: List<DetalleVentaEntity>) {
        try {
            // Convertimos las entidades de Room a DTOs para Retrofit
            val productosDto = detalles.map {
                DetalleVentaDto(producto_id = it.producto_id, cantidad = it.cantidad)
            }
            val request = VentaRequest(productos = productosDto)

            val response = api.registrarVenta(request)

            if (response.isSuccessful) {
                // Si el servidor responde 201 Created, actualizamos la base de datos local
                ventaDao.marcarComoSincronizada(idLocal)
                // Disparar notificación
                notificationHelper.mostrarNotificacion(
                    titulo = "Sincronización Exitosa",
                    mensaje = "Se sincronizaron tus ventas con el servidor.",
                    notificationId = idLocal
                )
            }
        } catch (e: Exception) {
            Log.e("VentaRepository", "Falló la subida al servidor, se intentará luego. Error: ${e.message}")
        }
    }
}