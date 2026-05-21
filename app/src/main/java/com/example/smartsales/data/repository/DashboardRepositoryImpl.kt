package com.example.smartsales.data.repository

import com.example.smartsales.data.remote.SmartSalesApi
import com.example.smartsales.data.remote.dto.DashboardResponse
import com.example.smartsales.domain.repository.DashboardRepository
import javax.inject.Inject

class DashboardRepositoryImpl @Inject constructor(
    private val api: SmartSalesApi
) : DashboardRepository {

    override suspend fun obtenerMetricasNube(): Result<DashboardResponse> {
        return try {
            val response = api.obtenerMetricasDashboard()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error al obtener datos del servidor"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Sin conexión al servidor para el Dashboard"))
        }
    }
}