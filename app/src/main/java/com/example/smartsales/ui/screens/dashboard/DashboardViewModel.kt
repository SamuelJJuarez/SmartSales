package com.example.smartsales.ui.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartsales.domain.repository.ProductoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// El estado visual de nuestro panel de control
data class DashboardState(
    val totalProductosRegistrados: Int = 0,
    val valorTotalInventario: Double = 0.0,
    val productosBajoStock: Int = 0, // Productos con menos de 10 unidades
    val isLoading: Boolean = true
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val productoRepository: ProductoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardState())
    val uiState: StateFlow<DashboardState> = _uiState.asStateFlow()

    init {
        calcularMetricas()
    }

    private fun calcularMetricas() {
        viewModelScope.launch {
            // Escuchamos los cambios en Room en tiempo real
            productoRepository.obtenerProductosLocales().collect { listaProductos ->

                val totalItems = listaProductos.size

                // Sumamos (precio * stock) de todos los productos
                val valorInventario = listaProductos.sumOf { it.precio * it.stock }

                // Contamos cuántos productos están a punto de agotarse
                val bajoStock = listaProductos.count { it.stock < 10 }

                _uiState.update {
                    it.copy(
                        totalProductosRegistrados = totalItems,
                        valorTotalInventario = valorInventario,
                        productosBajoStock = bajoStock,
                        isLoading = false
                    )
                }
            }
        }
    }
}