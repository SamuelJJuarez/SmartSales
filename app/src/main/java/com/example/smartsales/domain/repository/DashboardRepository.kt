package com.example.smartsales.domain.repository

import com.example.smartsales.data.remote.dto.DashboardResponse

interface DashboardRepository {
    suspend fun obtenerMetricasNube(): Result<DashboardResponse>
}