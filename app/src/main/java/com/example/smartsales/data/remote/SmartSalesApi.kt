package com.example.smartsales.data.remote

import com.example.smartsales.data.remote.dto.LoginRequest
import com.example.smartsales.data.remote.dto.LoginResponse
import com.example.smartsales.data.remote.dto.ProductoDto
import com.example.smartsales.data.remote.dto.VentaRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface SmartSalesApi {

    // Endpoint de Login
    @POST("api/auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>

    // Endpoint para obtener todos los productos
    @GET("api/productos")
    suspend fun getProductos(): Response<List<ProductoDto>>

    // Aquí agregaremos después las ventas y el dashboard
    @POST("api/ventas")
    suspend fun registrarVenta(@Body request: VentaRequest): Response<Any>
}