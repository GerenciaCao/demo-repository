package com.example.gerenciacao.di

import android.content.Context
import com.example.gerenciacao.BuildConfig
import com.example.gerenciacao.data.local.SessaoManager
import com.example.gerenciacao.data.local.SessaoStore
import com.example.gerenciacao.data.remote.ApiFactory
import com.example.gerenciacao.data.repository.AbrigoRepositoryImpl
import com.example.gerenciacao.data.repository.AuthRepositoryImpl
import com.example.gerenciacao.data.repository.EquipeRepositoryImpl
import com.example.gerenciacao.domain.repository.AbrigoRepository
import com.example.gerenciacao.domain.repository.AuthRepository
import com.example.gerenciacao.domain.repository.EquipeRepository
import kotlinx.serialization.json.Json

/**
 * Injeção de dependências "manual": cria uma vez as peças do app e entrega para quem precisa.
 * As telas e ViewModels recebem só as INTERFACES de repositório (domain), nunca Retrofit ou token.
 */
class AppContainer(contexto: Context) {
    private val json = Json {
        ignoreUnknownKeys = true // a API pode ganhar campos novos sem quebrar o app
        explicitNulls = false
        encodeDefaults = false
    }
    private val sessaoManager = SessaoManager(SessaoStore(contexto))
    private val api = ApiFactory.criar(BuildConfig.API_BASE_URL, json, sessaoManager)

    val authRepository: AuthRepository = AuthRepositoryImpl(api, sessaoManager, json)
    val equipeRepository: EquipeRepository = EquipeRepositoryImpl(api, json)
    val abrigoRepository: AbrigoRepository = AbrigoRepositoryImpl(api, json)
}
