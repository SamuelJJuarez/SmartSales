package com.example.smartsales.data.mapper

import com.example.smartsales.data.local.entity.ProductoEntity
import com.example.smartsales.data.remote.dto.ProductoDto

// Función de extensión que convierte el DTO de la API a la Entidad de la BD Local
fun ProductoDto.toEntity(): ProductoEntity {
    return ProductoEntity(
        id = this.id ?: 0,
        codigo_barras = this.codigo_barras,
        nombre = this.nombre,
        descripcion = this.descripcion,
        precio = this.precio,
        stock = this.stock,
        activo = this.activo
    )
}