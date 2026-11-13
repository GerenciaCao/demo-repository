package com.example.gerenciacao.domain.model

/** Cargos do abrigo. O rótulo é o texto mostrado nas telas (sem flexão de gênero: "Cuidador"). */
enum class Cargo(val rotulo: String) {
    ADMINISTRADOR("Administrador"),
    GESTOR("Gestor"),
    CUIDADOR("Cuidador"),
    VETERINARIO("Veterinário"),
    ADESTRADOR("Adestrador"),
    VOLUNTARIO("Voluntário"),
}

/** Permissões de acesso de um membro (as mesmas 6 da tela de edição). */
enum class Permissao(val rotulo: String) {
    VER_FICHAS("Ver Fichas dos Animais"),
    EDITAR_PRONTUARIOS("Editar Prontuários"),
    GERENCIAR_AGENDA("Gerenciar Agenda"),
    GERENCIAR_DADOS("Gerenciar Dados"),
    MODIFICAR_EQUIPE("Modificar Equipe"),
    GERENCIAR_ESTOQUE("Gerenciar Estoque"),
}

/** Situação da conta. CONVIDADO = ainda não definiu a senha. */
enum class StatusConta { ATIVO, CONVIDADO, INATIVO }

/** Membro da equipe de um abrigo (também é o usuário logado). */
data class Membro(
    val id: String,
    val abrigoId: String,
    val nome: String,
    val sobrenome: String,
    val email: String,
    val telefone: String,
    val endereco: String,
    val cargo: Cargo,
    val permissoes: Set<Permissao>,
    val status: StatusConta,
) {
    /** "Nome Sobrenome" sem espaços sobrando. */
    val nomeCompleto: String get() = "$nome $sobrenome".trim()

    /** Confere se o membro tem a permissão. */
    fun pode(permissao: Permissao): Boolean = permissao in permissoes
}

/** Informações do abrigo. [capacidade] é a lotação máxima; as vagas são calculadas. */
data class Abrigo(
    val id: String,
    val nome: String,
    val descricao: String,
    val telefone: String,
    val email: String,
    val endereco: String,
    val redesSociais: String,
    val capacidade: Int,
    val animaisTotais: Int,
) {
    /** Vagas livres = capacidade - animais ativos (nunca negativo). */
    val vagas: Int get() = (capacidade - animaisTotais).coerceAtLeast(0)

    /** Percentual de ocupação de 0 a 100. */
    val ocupacaoPercentual: Int
        get() = if (capacidade <= 0) 0 else (animaisTotais * 100 / capacidade).coerceIn(0, 100)
}

/** Dados para convidar um novo membro. */
data class NovoMembro(
    val nome: String,
    val sobrenome: String,
    val email: String,
    val telefone: String,
    val endereco: String,
    val cargo: Cargo,
)

/** Campos que podem ser alterados em um membro (nulo = não muda). */
data class AtualizacaoMembro(
    val email: String? = null,
    val telefone: String? = null,
    val endereco: String? = null,
    val cargo: Cargo? = null,
    val permissoes: Set<Permissao>? = null,
)

/** Resultado de um convite: o código que a pessoa convidada usa para criar a senha. */
data class Convite(val membro: Membro, val codigo: String)

/** Estado da sessão do usuário no app. */
sealed interface EstadoSessao {
    /** Ainda verificando se existe sessão guardada (início do app). */
    data object Verificando : EstadoSessao

    /** Ninguém logado. */
    data object SemSessao : EstadoSessao

    /** Usuário logado. */
    data class Logado(val usuario: Membro) : EstadoSessao
}
