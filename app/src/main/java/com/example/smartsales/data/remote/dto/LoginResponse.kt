package com.example.smartsales.data.remote.dto

data class LoginResponse(
    val mensaje: String,
    val token: String,
    val usuario: UsuarioDto
)

data class UsuarioDto(
    val id: Int,
    val nombre: String,
    val rol: String
)