package com.example.smartsales.ui.screens.productos.gestion

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartsales.data.local.entity.ProductoEntity
import com.example.smartsales.domain.repository.ProductoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class GestionProductoViewModel @Inject constructor(
    private val repository: ProductoRepository
) : ViewModel() {

    var id by mutableStateOf<Int?>(null)
    var codigoBarras by mutableStateOf("")
    var nombre by mutableStateOf("")
    var descripcion by mutableStateOf("")
    var precio by mutableStateOf("")
    var stock by mutableStateOf("")
    var activo by mutableStateOf(true)

    var isLoading by mutableStateOf(false)
    var isSuccess by mutableStateOf(false)
    var error by mutableStateOf<String?>(null)

    // Cargar datos si es edición
    fun cargarProducto(producto: ProductoEntity) {
        id = producto.id
        codigoBarras = producto.codigo_barras
        nombre = producto.nombre
        descripcion = producto.descripcion ?: ""
        precio = producto.precio.toString()
        stock = producto.stock.toString()
        activo = producto.activo
    }

    fun guardar() {
        if (nombre.isBlank() || precio.isBlank() || stock.isBlank()) {
            error = "Completa los campos obligatorios"
            return
        }

        viewModelScope.launch {
            isLoading = true
            val entidad = ProductoEntity(
                id = id ?: 0,
                codigo_barras = codigoBarras,
                nombre = nombre,
                descripcion = descripcion,
                precio = precio.toDoubleOrNull() ?: 0.0,
                stock = stock.toIntOrNull() ?: 0,
                activo = activo
            )

            repository.guardarProducto(entidad, esEdicion = id != null)
                .onSuccess { isSuccess = true }
                .onFailure { error = it.message }

            isLoading = false
        }
    }

    fun limpiarCampos() {
        id = null
        codigoBarras = ""
        nombre = ""
        descripcion = ""
        precio = ""
        stock = ""
        activo = true
        error = null
        isSuccess = false
        isLoading = false
    }
}