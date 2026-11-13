package com.example.gerenciacao.data.remote

import com.example.gerenciacao.data.local.SessaoManager
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Interceptor do OkHttp: coloca "Authorization: Bearer <token>" em toda requisição quando há sessão
 * e, se o servidor responder 401 a um pedido autenticado (token vencido ou conta removida), encerra a sessão.
 */
class AuthInterceptor(private val sessao: SessaoManager) : Interceptor {

    /** Adiciona o token e confere a resposta. */
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val token = sessao.token()
        val requisicao = if (token != null && original.header("Authorization") == null) {
            original.newBuilder().header("Authorization", "Bearer $token").build()
        } else {
            original
        }
        val resposta = chain.proceed(requisicao)
        if (resposta.code == 401 && token != null) sessao.expirar()
        return resposta
    }
}
