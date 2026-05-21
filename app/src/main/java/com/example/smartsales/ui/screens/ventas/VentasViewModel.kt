package com.example.smartsales.ui.screens.ventas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartsales.data.local.entity.DetalleVentaEntity
import com.example.smartsales.domain.repository.ProductoRepository
import com.example.smartsales.domain.repository.VentaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VentasViewModel @Inject constructor(
    private val productoRepository: ProductoRepository,
    private val ventaRepository: VentaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(VentasState())
    val uiState: StateFlow<VentasState> = _uiState.asStateFlow()

    init {
        despacharVentasPendientes()
    }

    fun despacharVentasPendientes() {
        viewModelScope.launch {
            ventaRepository.sincronizarVentasPendientes()
        }
    }

    // 1. Agregar un producto al carrito escaneando su código
    fun agregarProductoPorCodigo(codigoBarras: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            // Buscamos el producto en la base de datos local
            val producto = productoRepository.obtenerProductoPorCodigo(codigoBarras)

            if (producto == null) {
                _uiState.update {
                    it.copy(isLoading = false, error = "Producto no encontrado: $codigoBarras")
                }
                return@launch
            }

            // Si el producto no tiene stock, no lo dejamos vender
            if (producto.stock <= 0) {
                _uiState.update {
                    it.copy(isLoading = false, error = "Sin stock disponible para: ${producto.nombre}")
                }
                return@launch
            }

            // Copiamos el carrito actual para modificarlo
            val carritoActual = _uiState.value.carrito.toMutableList()

            // Verificamos si el producto ya está en el carrito
            val indexExistente = carritoActual.indexOfFirst { it.producto.id == producto.id }

            if (indexExistente != -1) {
                // Si ya está, verificamos que no exceda el stock máximo al sumar 1
                val item = carritoActual[indexExistente]
                if (item.cantidad < producto.stock) {
                    carritoActual[indexExistente] = item.copy(cantidad = item.cantidad + 1)
                } else {
                    _uiState.update {
                        it.copy(isLoading = false, error = "Stock máximo alcanzado")
                    }
                    return@launch
                }
            } else {
                // Si no está, lo agregamos como nuevo
                carritoActual.add(ItemCarrito(producto, 1))
            }

            // Calculamos el nuevo total
            val nuevoTotal = carritoActual.sumOf { it.subtotal }

            // Actualizamos la pantalla
            _uiState.update {
                it.copy(
                    carrito = carritoActual,
                    total = nuevoTotal,
                    isLoading = false
                )
            }
        }
    }

    // 2. Aumentar la cantidad de un producto ya escaneado
    fun aumentarCantidad(productoId: Int) {
        val carritoActual = _uiState.value.carrito.toMutableList()
        val index = carritoActual.indexOfFirst { it.producto.id == productoId }

        if (index != -1) {
            val item = carritoActual[index]
            // Verificamos que no sobrepase el stock disponible en la tienda
            if (item.cantidad < item.producto.stock) {
                carritoActual[index] = item.copy(cantidad = item.cantidad + 1)
                actualizarCarritoYTotal(carritoActual)
            } else {
                _uiState.update { it.copy(error = "Stock máximo alcanzado para: ${item.producto.nombre}") }
            }
        }
    }

    // 3. Disminuir la cantidad (y eliminar si llega a 0)
    fun disminuirCantidad(productoId: Int) {
        val carritoActual = _uiState.value.carrito.toMutableList()
        val index = carritoActual.indexOfFirst { it.producto.id == productoId }

        if (index != -1) {
            val item = carritoActual[index]
            if (item.cantidad > 1) {
                // Si hay más de 1, simplemente restamos
                carritoActual[index] = item.copy(cantidad = item.cantidad - 1)
            } else {
                // Si es 1 y le damos al menos, lo sacamos del carrito
                carritoActual.removeAt(index)
            }
            actualizarCarritoYTotal(carritoActual)
        }
    }

    // 4. Función auxiliar para recalcular y repintar la pantalla
    private fun actualizarCarritoYTotal(nuevoCarrito: List<ItemCarrito>) {
        val nuevoTotal = nuevoCarrito.sumOf { it.subtotal }
        _uiState.update {
            it.copy(
                carrito = nuevoCarrito,
                total = nuevoTotal
            )
        }
    }


    // 5. Cobrar / Registrar la Venta (El botón final)
    fun registrarVenta() {
        val carritoActual = _uiState.value.carrito
        val totalActual = _uiState.value.total

        if (carritoActual.isEmpty()) {
            _uiState.update { it.copy(error = "El carrito está vacío") }
            return
        }

        _uiState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            // Mapeamos el "ItemCarrito" temporal a la Entidad de Base de Datos
            val detallesEntity = carritoActual.map { item ->
                DetalleVentaEntity(
                    ventaLocalId = 0, // Se asignará automáticamente en el Repositorio
                    producto_id = item.producto.id,
                    cantidad = item.cantidad,
                    precio_unitario = item.producto.precio,
                    subtotal = item.subtotal
                )
            }

            // Mandamos al Repositorio (Él decidirá si lo manda a Node.js o lo guarda offline)
            val result = ventaRepository.registrarVenta(detallesEntity, totalActual)

            result.onSuccess {
                // Si la venta se registró (ya sea online u offline), limpiamos el carrito
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isSuccess = true,
                        carrito = emptyList(),
                        total = 0.0
                    )
                }
            }

            result.onFailure { excepcion ->
                _uiState.update { it.copy(isLoading = false, error = excepcion.message) }
            }
        }
    }

    // Limpiar el estado de éxito para que el usuario pueda hacer otra venta
    fun reiniciarVenta() {
        _uiState.update { it.copy(isSuccess = false) }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}