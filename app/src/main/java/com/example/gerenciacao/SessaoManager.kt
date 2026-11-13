package com.example.gerenciacao.data.local

import com.example.gerenciacao.domain.model.EstadoSessao
import com.example.gerenciacao.domain.model.Membro
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** Dono do estado da sessão: guarda o token (em memória e cifrado no disco) e avisa quem estiver observando. */
class SessaoManager(private val store: SessaoStore) {
    @Volatile private var tokenEmMemoria: String? = store.lerToken()
    private val _estado = MutableStateFlow<EstadoSessao>(EstadoSessao.Verificando)

    /** Estado atual da sessão. */
    val estado: StateFlow<EstadoSessao> = _estado.asStateFlow()

    /** Token atual (ou null se não há sessão). Usado pelo interceptor em cada requisição. */
    fun token(): String? = tokenEmMemoria

    /** Começa uma sessão: guarda o token e marca o usuário como logado. */
    fun iniciar(token: String, usuario: Membro) {
        store.salvarToken(token)
        tokenEmMemoria = token
        _estado.value = EstadoSessao.Logado(usuario)
    }

    /** Atualiza os dados do usuário logado (ex.: depois de restaurar a sessão). */
    fun definirUsuario(usuario: Membro) {
        _estado.value = EstadoSessao.Logado(usuario)
    }

    /** Declara que não há sessão (sem apagar nada: usado quando nunca houve login). */
    fun semSessao() {
        _estado.value = EstadoSessao.SemSessao
    }

    /** Encerra a sessão: apaga o token e volta ao estado "sem sessão". */
    fun encerrar() {
        store.limpar()
        tokenEmMemoria = null
        _estado.value = EstadoSessao.SemSessao
    }

    /** O servidor recusou o token (vencido/conta removida): encerra a sessão. */
    fun expirar() = encerrar()
}
