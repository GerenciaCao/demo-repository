package com.example.gerenciacao.domain.repository

import com.example.gerenciacao.domain.Resultado
import com.example.gerenciacao.domain.model.Abrigo
import com.example.gerenciacao.domain.model.AtualizacaoMembro
import com.example.gerenciacao.domain.model.Convite
import com.example.gerenciacao.domain.model.EstadoSessao
import com.example.gerenciacao.domain.model.Membro
import com.example.gerenciacao.domain.model.NovoMembro
import kotlinx.coroutines.flow.StateFlow

/** Contrato de autenticação e sessão. A tela só conhece esta interface, nunca o Retrofit. */
interface AuthRepository {
    /** Estado atual da sessão (observável). */
    val estado: StateFlow<EstadoSessao>

    /** Entra com e-mail e senha. */
    suspend fun login(email: String, senha: String): Resultado<Membro>

    /** Cria a conta (e o abrigo) e já entra. */
    suspend fun cadastrar(nome: String, email: String, telefone: String, senha: String): Resultado<Membro>

    /** Conclui um convite com o código recebido e já entra. */
    suspend fun aceitarConvite(codigo: String, senha: String): Resultado<Membro>

    /** Se existe token guardado, confere com o servidor e restaura a sessão. */
    suspend fun restaurarSessao()

    /** Encerra a sessão neste aparelho. */
    fun sair()
}

/** Contrato da equipe do abrigo. */
interface EquipeRepository {
    suspend fun listar(): Resultado<List<Membro>>
    suspend fun buscar(id: String): Resultado<Membro>
    suspend fun convidar(novo: NovoMembro): Resultado<Convite>
    suspend fun atualizar(id: String, alteracao: AtualizacaoMembro): Resultado<Membro>
    suspend fun remover(id: String): Resultado<Unit>
}

/** Contrato das informações do abrigo. */
interface AbrigoRepository {
    suspend fun buscar(): Resultado<Abrigo>
}
