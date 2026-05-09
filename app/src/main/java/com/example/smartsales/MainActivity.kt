package com.example.smartsales

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.smartsales.ui.navigation.AppNavigation
import com.example.smartsales.ui.theme.SmartSalesTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint //
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SmartSalesTheme {
                // Surface es un contenedor base que ocupará toda la pantalla
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Aquí mandaremos llamar a nuestro gestor de rutas
                    AppNavigation()
                }
            }
        }
    }
}