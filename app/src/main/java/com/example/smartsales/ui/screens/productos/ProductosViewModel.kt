package com.example.smartsales.ui.screens.productos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartsales.data.local.entity.ProductoEntity
import com.example.smartsales.domain.repository.ProductoRepository
import com.example.smartsales.domain.repository.VentaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductosViewModel @Inject constructor(
    private val productoRepository: ProductoRepository,
    private val ventaRepository: VentaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProductosState())
    val uiState: StateFlow<ProductosState> = _uiState.asStateFlow()

    private var todosLosProductos: List<ProductoEntity> = emptyList()

    init {
        // En cuanto se crea la pantalla, empezamos a observar la base de datos local
        observarProductosLocales()
        // Y forzamos una sincronización con Node.js
        sincronizarConServidor()
    }

    private fun observarProductosLocales() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            productoRepository.obtenerProductosLocales()
                .catch { excepcion ->
                    _uiState.update { it.copy(isLoading = false, error = excepcion.message) }
                }
                .collect { listaProductos ->
                    // Guardamos la lista original
                    todosLosProductos = listaProductos

                    // Aplicamos el filtro inmediatamente por si el usuario ya había escrito algo
                    aplicarFiltro(_uiState.value.searchQuery)

                    // Cada vez que Room cambie, esta lista se actualiza automáticamente
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            productos = listaProductos
                        )
                    }
                }
        }
    }

    // Función para que la UI le avise al ViewModel cuando el texto cambie
    fun onSearchQueryChange(newQuery: String) {
        _uiState.update { it.copy(searchQuery = newQuery) }
        aplicarFiltro(newQuery)
    }

    // Lógica de filtrado
    private fun aplicarFiltro(query: String) {
        val productosFiltrados = if (query.isBlank()) {
            todosLosProductos
        } else {
            todosLosProductos.filter {
                // Buscamos ignorando mayúsculas/minúsculas en el nombre o coincidencia en código
                it.nombre.contains(query, ignoreCase = true) ||
                        it.codigo_barras.contains(query)
            }
        }

        _uiState.update { it.copy(productos = productosFiltrados) }
    }


    // Se llama al inicio o cuando el usuario hace un "Pull to Refresh"
    fun sincronizarConServidor() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }

            try {
                ventaRepository.sincronizarVentasPendientes()
                productoRepository.sincronizarProductos()
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "No se pudo sincronizar. Mostrando datos locales.") }
            } finally {
                _uiState.update { it.copy(isRefreshing = false) }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}