package com.example.smartsales.di

import android.content.Context
import androidx.room.Room
import com.example.smartsales.data.local.SmartSalesDatabase
import com.example.smartsales.data.local.dao.ProductoDao
import com.example.smartsales.data.local.dao.VentaDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideSmartSalesDatabase(@ApplicationContext context: Context): SmartSalesDatabase {
        return Room.databaseBuilder(
            context,
            SmartSalesDatabase::class.java,
            "smartsales_local_db"
        ).fallbackToDestructiveMigration() // Si cambias una tabla, borra y recrea la BD (ideal para desarrollo)
            .build()
    }

    @Provides
    @Singleton
    fun provideProductoDao(database: SmartSalesDatabase): ProductoDao {
        return database.productoDao
    }

    @Provides
    @Singleton
    fun provideVentaDao(database: SmartSalesDatabase): VentaDao {
        return database.ventaDao
    }
}