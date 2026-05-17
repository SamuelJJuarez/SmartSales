package com.example.smartsales.ui.screens.login

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.smartsales.ui.navigation.Routes

@Composable
fun LoginScreen(
    navController: NavController,
    // Hilt inyecta automáticamente la instancia del ViewModel aquí
    viewModel: LoginViewModel = hiltViewModel()
) {
    // 1. Recolectar los estados reactivos del ViewModel de forma segura para el ciclo de vida
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val email by viewModel.email.collectAsStateWithLifecycle()
    val password by viewModel.password.collectAsStateWithLifecycle()

    // 2. Estado para manejar el Snackbar (alertas amigables)
    val snackbarHostState = remember { SnackbarHostState() }

    // 3. Efectos Secundarios (Navegación y Errores)
    // Se ejecuta automáticamente si isSuccess cambia a true
    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            // Navegamos al Dashboard y borramos el Login del historial
            // para que el usuario no regrese al presionar "Atrás"
            //navController.navigate(Routes.Dashboard.route) {
              //  popUpTo(Routes.Login.route) { inclusive = true }
            //}
            //navController.navigate(Routes.Productos.route) {
                //popUpTo(Routes.Login.route) { inclusive = true }
            //}
            navController.navigate(Routes.Ventas.route) {
                popUpTo(Routes.Login.route) { inclusive = true }
            }
        }
    }

    // Se ejecuta automáticamente si hay un nuevo mensaje de error
    LaunchedEffect(uiState.error) {
        uiState.error?.let { errorMsg ->
            snackbarHostState.showSnackbar(message = errorMsg)
            viewModel.clearError() // Limpiamos el error después de mostrarlo
        }
    }

    // 4. El Diseño Visual de la Pantalla (UI)
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Título de la App
            Text(
                text = "SmartSales",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Campo de Correo
            OutlinedTextField(
                value = email,
                onValueChange = viewModel::onEmailChange,
                label = { Text("Correo Electrónico") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = !uiState.isLoading // Se bloquea si está cargando
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campo de Contraseña
            OutlinedTextField(
                value = password,
                onValueChange = viewModel::onPasswordChange,
                label = { Text("Contraseña") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(), // Oculta el texto con puntos
                enabled = !uiState.isLoading
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Botón de Ingreso
            Button(
                onClick = { viewModel.login() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = !uiState.isLoading
            ) {
                if (uiState.isLoading) {
                    // Muestra el círculo de carga mientras espera la respuesta del servidor
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Ingresar")
                }
            }
        }
    }
}