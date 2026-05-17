package com.example.smartsales.di

import com.example.smartsales.data.repository.AuthRepositoryImpl
import com.example.smartsales.data.repository.ProductoRepositoryImpl
import com.example.smartsales.data.repository.VentaRepositoryImpl
import com.example.smartsales.domain.repository.AuthRepository
import com.example.smartsales.domain.repository.ProductoRepository
import com.example.smartsales.domain.repository.VentaRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindProductoRepository(
        productoRepositoryImpl: ProductoRepositoryImpl
    ): ProductoRepository

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindVentaRepository(
        ventaRepositoryImpl: VentaRepositoryImpl
    ): VentaRepository
}