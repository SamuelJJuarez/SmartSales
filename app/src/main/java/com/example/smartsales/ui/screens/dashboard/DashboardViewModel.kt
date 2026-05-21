package com.example.smartsales.ui.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartsales.data.remote.dto.ProductoTopDto
import com.example.smartsales.data.remote.dto.VentaDiaDto
import com.example.smartsales.domain.repository.DashboardRepository
import com.example.smartsales.domain.repository.ProductoRepository
import com.example.smartsales.util.NotificationHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// Actualizamos el estado para incluir los datos de la API
data class DashboardState(
    val ingresosTotalesNube: Double = 0.0,
    val topProductos: List<ProductoTopDto> = emptyList(),
    val totalProductosLocales: Int = 0,
    val productosBajoStock: Int = 0,
    val isLoading: Boolean = true,
    val error: String? = null,
    val ventasPorDia: List<VentaDiaDto> = emptyList()
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val productoRepository: ProductoRepository,
    private val dashboardRepository: DashboardRepository, // Inyectamos el nuevo repo
    private val notificationHelper: NotificationHelper
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardState())
    val uiState: StateFlow<DashboardState> = _uiState.asStateFlow()

    init {
        cargarDatos()
    }

    fun cargarDatos() {
        _uiState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            // 1. Cargamos datos de la nube (Ingresos y Top Ventas)
            dashboardRepository.obtenerMetricasNube()
                .onSuccess { datosNube ->
                    _uiState.update {
                        it.copy(
                            ingresosTotalesNube = datosNube.ingresos_totales,
                            topProductos = datosNube.productos_top,
                            ventasPorDia = datosNube.ventas_por_dia
                        )
                    }
                }
                .onFailure { excepcion ->
                    _uiState.update { it.copy(error = excepcion.message) }
                }

            // 2. Cargamos datos locales (Stock y Alertas)
            productoRepository.obtenerProductosLocales().collect { lista ->
                val bajoStock = lista.count { it.stock < 10 && it.activo }

                if (bajoStock > 0) {
                    notificationHelper.mostrarNotificacion(
                        titulo = "Alerta de Inventario",
                        mensaje = "Tienes $bajoStock producto(s) con bajo stock.",
                        notificationId = 999
                    )
                }

                _uiState.update {
                    it.copy(
                        totalProductosLocales = lista.filter { p -> p.activo }.size,
                        productosBajoStock = bajoStock,
                        isLoading = false
                    )
                }
            }
        }
    }
}