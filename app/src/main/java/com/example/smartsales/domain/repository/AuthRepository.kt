package com.example.smartsales.domain.repository

interface AuthRepository {
    // Usamos Result de Kotlin para manejar el éxito o el fracaso fácilmente
    suspend fun login(email: String, password: String): Result<Unit>
}