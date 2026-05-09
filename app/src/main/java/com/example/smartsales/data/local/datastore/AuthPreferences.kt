package com.example.smartsales.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

// Esto crea la instancia única de DataStore ligada al contexto de la app
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_prefs")

class AuthPreferences @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        // La "llave" con la que guardaremos el token
        val TOKEN_KEY = stringPreferencesKey("jwt_token")
    }

    // Un Flow que emite el token automáticamente si cambia (Reactividad pura)
    val getToken: Flow<String?> = dataStore.data.map { preferences ->
        preferences[TOKEN_KEY]
    }

    // Función suspendida para guardar el token cuando el login sea exitoso
    suspend fun saveToken(token: String) {
        dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = token
        }
    }

    // Función para borrar el token (Cerrar sesión)
    suspend fun clearToken() {
        dataStore.edit { preferences ->
            preferences.remove(TOKEN_KEY)
        }
    }
}