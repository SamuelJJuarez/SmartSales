package com.example.smartsales.ui.screens.login

data class LoginState(
    val isLoading: Boolean = false, // Para mostrar el circulito de carga
    val error: String? = null,      // Para mostrar un Snackbar si falla
    val isSuccess: Boolean = false  // Para saber cuándo navegar al Dashboard
)