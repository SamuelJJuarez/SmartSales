package com.example.smartsales.data.repository

import com.example.smartsales.data.local.datastore.AuthPreferences
import com.example.smartsales.data.remote.SmartSalesApi
import com.example.smartsales.data.remote.dto.LoginRequest
import com.example.smartsales.domain.repository.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val api: SmartSalesApi,
    private val authPreferences: AuthPreferences
) : AuthRepository {

    override suspend fun login(email: String, password: String): Result<Unit> {
        return try {
            val request = LoginRequest(email, password)
            val response = api.login(request)

            if (response.isSuccessful && response.body() != null) {
                // Si Express nos da un 200 OK, guardamos el token en DataStore
                val token = response.body()!!.token
                authPreferences.saveToken(token)
                Result.success(Unit)
            } else {
                // Si es un 401 (Credenciales inválidas)
                Result.failure(Exception("Correo o contraseña incorrectos"))
            }
        } catch (e: Exception) {
            // Si no hay internet o el servidor está caído
            Result.failure(Exception("Error de conexión con el servidor"))
        }
    }
}