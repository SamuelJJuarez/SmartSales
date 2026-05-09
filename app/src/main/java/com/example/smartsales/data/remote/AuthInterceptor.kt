package com.example.smartsales.data.remote

import com.example.smartsales.data.local.datastore.AuthPreferences
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val authPreferences: AuthPreferences
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        // OkHttp funciona de manera síncrona, pero DataStore usa Coroutines (asíncrono).
        // runBlocking nos permite leer el último token disponible de forma segura.
        val token = runBlocking {
            authPreferences.getToken.first()
        }

        val requestBuilder = chain.request().newBuilder()

        // Si tenemos un token guardado, lo inyectamos con el formato "Bearer <token>"
        if (!token.isNullOrEmpty()) {
            requestBuilder.addHeader("Authorization", "Bearer $token")
        }

        // Dejamos que la petición continúe su camino
        return chain.proceed(requestBuilder.build())
    }
}