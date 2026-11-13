package com.example.gerenciacao.data.remote

import com.example.gerenciacao.data.local.SessaoManager
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit

/** Monta o cliente HTTP (OkHttp) e o Retrofit. Não registra o corpo das requisições (têm senha e token). */
object ApiFactory {

    /** Cria o [ApiService] apontando para [baseUrl] (termina com "/"). */
    fun criar(baseUrl: String, json: Json, sessao: SessaoManager): ApiService {
        val cliente = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(sessao))
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .build()
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(cliente)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(ApiService::class.java)
    }
}
