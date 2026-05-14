package com.example.smartsales.ui.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartsales.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.smartsales.data.local.datastore.AuthPreferences
import kotlinx.coroutines.flow.first
import com.example.smartsales.util.NetworkUtils

@HiltViewModel // Fundamental para que Hilt le inyecte el repositorio
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val authPreferences: AuthPreferences,
    private val networkUtils: NetworkUtils
) : ViewModel() {

    // 1. Estado para el correo y la contraseña
    var email = MutableStateFlow("")
        private set

    var password = MutableStateFlow("")
        private set

    // 2. Estado de la pantalla (Carga, Éxito, Error)
    private val _uiState = MutableStateFlow(LoginState())
    val uiState: StateFlow<LoginState> = _uiState.asStateFlow()

    // El bloque init se ejecuta apenas se crea el ViewModel
    init {
        verificarSesionGuardada()
    }

    // Función para saltar el login si ya tenemos token
    private fun verificarSesionGuardada() {
        viewModelScope.launch {
            // Leemos el token actual de DataStore
            val token = authPreferences.getToken.first()

            // Solo navegamos directo al catálogo si NO hay internet Y existe un token.
            // Si hay internet (isNetworkAvailable() es true), la condición no se cumple
            // y el usuario se queda en la pantalla de Login obligatoriamente.
            if (!networkUtils.isNetworkAvailable() && !token.isNullOrEmpty()) {
                _uiState.update { it.copy(isSuccess = true) }
            }
        }
    }

    // 3. Eventos que vienen desde la interfaz de usuario (Compose)
    fun onEmailChange(newEmail: String) {
        email.value = newEmail
    }

    fun onPasswordChange(newPassword: String) {
        password.value = newPassword
    }

    // 4. La función que se ejecuta al presionar el botón "Ingresar"
    fun login() {
        // Validación básica
        if (email.value.isBlank() || password.value.isBlank()) {
            _uiState.update { it.copy(error = "Por favor llena todos los campos") }
            return
        }

        // Mostramos el círculo de carga y limpiamos errores previos
        _uiState.update { it.copy(isLoading = true, error = null) }

        // Lanzamos una corrutina para hacer la petición a internet sin congelar la app
        viewModelScope.launch {
            val result = authRepository.login(email.value, password.value)

            result.onSuccess {
                // Si va bien, detenemos la carga y marcamos el éxito
                _uiState.update { it.copy(isLoading = false, isSuccess = true) }
            }

            result.onFailure { excepcion ->
                // Si falla, detenemos la carga y mandamos el mensaje de error para el Snackbar
                _uiState.update { it.copy(isLoading = false, error = excepcion.message) }
            }
        }
    }

    // Función para limpiar el error después de que el Snackbar ya lo mostró
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}