package com.example.gerenciacao.domain

/** Tipos de erro que as telas sabem tratar. */
enum class TipoErro { SEM_CONEXAO, NAO_AUTORIZADO, SEM_PERMISSAO, NAO_ENCONTRADO, CONFLITO, VALIDACAO, MUITAS_TENTATIVAS, SERVIDOR, DESCONHECIDO }

/** Erro já traduzido para o app: [mensagem] pode ser mostrada ao usuário. */
data class ErroApp(
    val tipo: TipoErro,
    val mensagem: String,
    val campos: Map<String, String> = emptyMap(),
) {
    /** Texto para exibir: se há erros por campo, junta as mensagens; senão usa a mensagem geral. */
    fun textoParaUsuario(): String = if (campos.isNotEmpty()) campos.values.joinToString("\n") else mensagem
}

/** Resultado de uma operação: deu certo (com dados) ou deu erro. Evita lançar exceções para as telas. */
sealed interface Resultado<out T> {
    data class Sucesso<T>(val dados: T) : Resultado<T>
    data class Erro(val erro: ErroApp) : Resultado<Nothing>
}
