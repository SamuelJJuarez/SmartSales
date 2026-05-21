package com.example.smartsales.data.remote

import com.example.smartsales.data.remote.dto.DashboardResponse
import com.example.smartsales.data.remote.dto.LoginRequest
import com.example.smartsales.data.remote.dto.LoginResponse
import com.example.smartsales.data.remote.dto.ProductoDto
import com.example.smartsales.data.remote.dto.VentaRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface SmartSalesApi {

    // Endpoint de Login
    @POST("api/auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>

    // Endpoint para obtener todos los productos
    @GET("api/productos")
    suspend fun getProductos(): Response<List<ProductoDto>>

    @POST("api/ventas")
    suspend fun registrarVenta(@Body request: VentaRequest): Response<Any>

    @POST("api/productos")
    suspend fun crearProducto(@Body producto: ProductoDto): Response<ProductoDto>

    @PUT("api/productos/{id}")
    suspend fun actualizarProducto(
        @Path("id") id: Int,
        @Body producto: ProductoDto
    ): Response<ProductoDto>

    // Endpoint del Dashboard
    @GET("api/dashboard")
    suspend fun obtenerMetricasDashboard(): Response<DashboardResponse>
}