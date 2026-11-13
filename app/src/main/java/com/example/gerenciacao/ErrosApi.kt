package com.example.gerenciacao.data.remote

import android.util.Log
import com.example.gerenciacao.BuildConfig
import com.example.gerenciacao.domain.ErroApp
import com.example.gerenciacao.domain.Resultado
import com.example.gerenciacao.domain.TipoErro
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.json.Json
import retrofit2.HttpException
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.net.UnknownServiceException
import javax.net.ssl.SSLException

/** Executa uma chamada da API e converte exceções em [Resultado.Erro] (cancelamento de coroutine continua passando). */
internal suspend fun <T> chamarApi(json: Json, bloco: suspend () -> T): Resultado<T> =
    try {
        Resultado.Sucesso(bloco())
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Resultado.Erro(e.paraErroApp(json))
    }

/** Traduz uma exceção de rede/HTTP para o [ErroApp] que as telas entendem. Nunca expõe detalhes técnicos. */
internal fun Exception.paraErroApp(json: Json): ErroApp = when (this) {
    is HttpException -> {
        val corpo = runCatching { response()?.errorBody()?.string() }.getOrNull()
        val dto = corpo?.let { runCatching { json.decodeFromString<ErroDto>(it) }.getOrNull() }
        ErroApp(
            tipo = tipoDoStatus(code()),
            mensagem = dto?.erro?.takeIf { it.isNotBlank() } ?: mensagemPadrao(code()),
            campos = dto?.campos.orEmpty(),
        )
    }
    is IOException -> ErroApp(TipoErro.SEM_CONEXAO, mensagemDeRede(this))
    else -> ErroApp(TipoErro.DESCONHECIDO, "Algo deu errado. Tente novamente.")
}

/** Status HTTP -> tipo de erro. */
private fun tipoDoStatus(codigo: Int): TipoErro = when (codigo) {
    400 -> TipoErro.VALIDACAO
    401 -> TipoErro.NAO_AUTORIZADO
    403 -> TipoErro.SEM_PERMISSAO
    404 -> TipoErro.NAO_ENCONTRADO
    409 -> TipoErro.CONFLITO
    429 -> TipoErro.MUITAS_TENTATIVAS
    in 500..599 -> TipoErro.SERVIDOR
    else -> TipoErro.DESCONHECIDO
}

/** Mensagem usada quando a API não mandou uma. */
private fun mensagemPadrao(codigo: Int): String = when (codigo) {
    401 -> "Sessão expirada. Entre novamente."
    403 -> "Você não tem permissão para esta ação."
    404 -> "Não encontramos o que você procura."
    429 -> "Muitas tentativas. Aguarde alguns minutos."
    in 500..599 -> "O servidor está com problemas. Tente mais tarde."
    else -> "Não foi possível concluir. Tente novamente."
}

/**
 * Mensagem para falhas de rede, conforme o tipo. No build DEBUG acrescenta o motivo técnico (e escreve no Logcat,
 * filtro "GerenciaCaoApi") para facilitar achar o problema; no release mostra só o texto amigável.
 */
private fun mensagemDeRede(e: IOException): String {
    val base = when (e) {
        is UnknownHostException -> "Não encontrei o servidor. Confira o endereço da API."
        is SocketTimeoutException -> "O servidor demorou demais para responder. Tente de novo."
        is ConnectException -> "Não consegui conectar ao servidor. Ele está ligado?"
        is SSLException -> "Falha na conexão segura com o servidor."
        is UnknownServiceException -> "O app não pôde usar essa conexão (sem HTTPS)."
        else -> "Sem conexão com o servidor. Verifique a internet e tente de novo."
    }
    if (!BuildConfig.DEBUG) return base
    Log.w("GerenciaCaoApi", "Falha de rede: ${e.javaClass.simpleName}: ${e.message}")
    return "$base\n[debug] ${e.javaClass.simpleName}: ${e.message}"
}